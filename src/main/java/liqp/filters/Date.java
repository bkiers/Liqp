package liqp.filters;

import liqp.filters.date.CustomDateFormatSupport;
import liqp.filters.date.StrftimeCompatibleDate;
import liqp.filters.date.StrftimeDateFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static liqp.filters.date.StrftimeCompatibleDate.datePatterns;

public class Date extends Filter {

    private static Locale locale = Locale.ENGLISH;

    private static List<CustomDateFormatSupport> supportedTypes = new ArrayList<>();

    public static void setLocale(Locale locale) {
        Date.locale = locale;
    }

    protected Date() {
        super();
    }

    protected Date(CustomDateFormatSupport typeSupport) {
        super();
        supportedTypes.add(0, typeSupport);
    }

    @Override
    public Object apply(Object value, Object... params) {

        try {
            final StrftimeCompatibleDate compatibleDate;

            if("now".equals(super.asString(value)) || "today".equals(super.asString(value))) {
                compatibleDate = new StrftimeCompatibleDate();
            } else if (isCustomDateType(value)) {
                compatibleDate = getFromCustomType(value);
            }
            else if(super.isNumber(value)) {
                // No need to divide this by 1000, the param is expected to be in seconds already!
                compatibleDate = new StrftimeCompatibleDate(super.asNumber(value).longValue() * 1000);
            }  else {
                compatibleDate = StrftimeCompatibleDate.parse(super.asString(value), locale);
                if(compatibleDate == null) {
                    return value;
                }
            }

            final String format = super.asString(super.get(0, params));

            if(format == null || format.trim().isEmpty()) {
                return value;
            }

            StrftimeDateFormatter formatter = new StrftimeDateFormatter(locale);
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

    private StrftimeCompatibleDate getFromCustomType(Object value) {
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
