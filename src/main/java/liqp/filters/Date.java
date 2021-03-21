package liqp.filters;

import liqp.LValue;
import liqp.TemplateContext;
import liqp.filters.date.CustomDateFormatRegistry;
import liqp.filters.date.CustomDateFormatSupport;
import liqp.filters.date.Parser;
import ua.co.k.strftime.StrftimeFormatter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

import static liqp.filters.date.Parser.datePatterns;

// general liquid info:
// https://shopify.github.io/liquid/filters/date/
public class Date extends Filter {

    protected Date() {
        super();
    }

    protected Date(CustomDateFormatSupport typeSupport) {
        super();
        CustomDateFormatRegistry.add(typeSupport);
    }


    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        Locale locale = context.renderSettings.locale;

        if (isArray(value) && asArray(value).length ==1) {
            value = asArray(value)[0];
        }
        try {
            final ZonedDateTime compatibleDate;
            if ("now".equals(super.asString(value)) || "today".equals(super.asString(value))) {
                compatibleDate = ZonedDateTime.now();
            } else if (LValue.isTemporal(value)) {
                compatibleDate = LValue.asTemporal(value);
            } else if(super.isNumber(value)) {
                // No need to divide this by 1000, the param is expected to be in seconds already!
                compatibleDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(super.asNumber(value).longValue() * 1000), ZoneId.systemDefault());
            } else {
                compatibleDate = Parser.parse(super.asString(value), locale);
            }
            if (compatibleDate == null) {
                return value;
            }

            final String format = super.asString(super.get(0, params));

            if(format == null || format.trim().isEmpty()) {
                // todo: verify this
                return value;
            }

            StrftimeFormatter formatter = StrftimeFormatter.ofSafePattern(format, locale);
            return formatter.format(compatibleDate);
        }
        catch (Exception e) {
            return value;
        }
    }

    /**
     * Adds a new Date-pattern to be used when parsing a string to a Date.
     *
     * @param pattern the pattern.
     */
    public static void addDatePattern(String pattern) {

        if(pattern == null) {
            throw new NullPointerException("date-pattern cannot be null");
        }

        datePatterns.add(pattern);
    }

    /**
     * Removed a Date-pattern to be used when parsing a string to a Date.
     *
     * @param pattern the pattern.
     */
    public static void removeDatePattern(String pattern) {
        datePatterns.remove(pattern);
    }

    public static Filter withCustomDateType(CustomDateFormatSupport typeSupport) {
        return new Date(typeSupport);
    }
}
