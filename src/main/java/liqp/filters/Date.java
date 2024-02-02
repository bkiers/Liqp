package liqp.filters;

import liqp.LValue;
import liqp.TemplateContext;
import liqp.filters.date.CustomDateFormatRegistry;
import liqp.filters.date.CustomDateFormatSupport;
import liqp.filters.date.Parser;
import ua.co.k.strftime.StrftimeFormatter;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Locale;

import static liqp.filters.date.Parser.datePatterns;

// general liquid info:
// https://shopify.github.io/liquid/filters/date/
public class Date extends Filter {

    protected Date() {
        super();
    }

    protected Date(CustomDateFormatSupport<?> typeSupport) {
        super();
        CustomDateFormatRegistry.add(typeSupport);
    }


    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {
        Locale locale = context.getParser().locale;

        if (isArray(value) && asArray(value, context).length ==1) {
            value = asArray(value, context)[0];
        }
        try {
            final ZonedDateTime compatibleDate;
            String valAsString = super.asString(value, context);
            if ("now".equals(valAsString) || "today".equals(valAsString)) {
                compatibleDate = ZonedDateTime.now();
            } else if (LValue.isTemporal(value)) {
                compatibleDate = LValue.asTemporal(value, context);
            } else if(super.isNumber(value)) {
                // No need to divide this by 1000, the param is expected to be in seconds already!
                compatibleDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(super.asNumber(value).longValue() * 1000), context.getParser().defaultTimeZone);
            } else {
                compatibleDate = Parser.parse(valAsString, locale, context.getParser().defaultTimeZone);
            }
            if (compatibleDate == null) {
                return value;
            }

            final String format = super.asString(super.get(0, params), context);

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

    public static Filter withCustomDateType(CustomDateFormatSupport<?> typeSupport) {
        return new Date(typeSupport);
    }
}
