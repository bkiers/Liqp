package liqp.filters;

import liqp.TemplateContext;
import liqp.filters.date.CustomDateFormatSupport;
import liqp.filters.date.Parser;
import liqp.filters.date.StrftimeDateFormatter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static liqp.filters.date.Parser.datePatterns;
import static liqp.filters.date.Parser.getZonedDateTimeFromTemporalAccessor;

// general liquid info:
// https://shopify.github.io/liquid/filters/date/
public class Date extends Filter {

    private static List<CustomDateFormatSupport> supportedTypes = new ArrayList<>();

    protected Date() {
        super();
    }

    protected Date(CustomDateFormatSupport typeSupport) {
        super();
        supportedTypes.add(0, typeSupport);
    }


    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        Locale locale = context.renderSettings.locale;

        try {
            final ZonedDateTime compatibleDate;
            if (value instanceof TemporalAccessor) { // this includes all java.time types, including ZonedDateTime itself
                compatibleDate = getZonedDateTimeFromTemporalAccessor((TemporalAccessor)value);
            } else if("now".equals(super.asString(value)) || "today".equals(super.asString(value))) {
                compatibleDate = ZonedDateTime.now();
            } else if (isCustomDateType(value)) {
                compatibleDate = getFromCustomType(value);
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

            StrftimeDateFormatter formatter = StrftimeDateFormatter.getInstance(locale);
            return formatter.format(format, compatibleDate);
        }
        catch (Exception e) {
            return value;
        }
    }

    private boolean isCustomDateType(Object value) {
        for (CustomDateFormatSupport el: supportedTypes) {
            if (el.support(value)) {
                return true;
            }
        }
        return false;
    }

    private ZonedDateTime getFromCustomType(Object value) {
        for (CustomDateFormatSupport el: supportedTypes) {
            if (el.support(value)) {
                return el.getValue(value);
            }
        }
        throw new UnsupportedOperationException();
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

    /**
     * use with caution.
     */
    public static void addCustomDateType(CustomDateFormatSupport typeSupport) {
        supportedTypes.add(0, typeSupport);
    }
}
