package liqp.filters;

import liqp.Template;
import liqp.filters.date.StrftimeDateFormatter;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Date extends Filter {
    private static Locale locale = Locale.ENGLISH;
    private static Set<String> datePatterns = new HashSet<>();

    static {
        addDatePattern("yyyy-MM-dd HH:mm:ss");
        addDatePattern("EEE MMM dd hh:mm:ss yyyy");
    }

    private CustomDateFormatSupport typeSupport;

    protected Date() {
        super();
    }

    protected Date(CustomDateFormatSupport typeSupport) {
        super();
        this.typeSupport = typeSupport;
    }

    @Override
    public Object apply(Object value, Object... params) {
        try {
            final Long seconds;

            if (super.asString(value).equals("now")) {
                seconds = System.currentTimeMillis() / 1000L;
            } else if (isCustomDateType(value)) {
                seconds = getFromCustomType(value);
            } else if (super.isNumber(value)) {
                // No need to divide this by 1000, the param is expected to be in seconds already!
                seconds = super.asNumber(value).longValue();
            } else {
                seconds = trySeconds(super.asString(value));
                if (seconds == null) {
                    return value;
                }
            }
            final DateTime dt = new DateTime(seconds * 1000L);
            final String format = super.asString(super.get(0, params));
            if(format == null || format.trim().isEmpty()) {
                return value;
            }
            StrftimeDateFormatter formatter = new StrftimeDateFormatter();
            return formatter.compileAndFormat(format, dt);
        } catch(Exception e) {
            return value;
        }
    }

    private boolean isCustomDateType(Object value) {
        return typeSupport != null && typeSupport.support(value);
    }

    private Long getFromCustomType(Object value) {
        return typeSupport.getAsSeconds(value);
    }

    /*
     * Try to parse `str` into a Date and return this Date as seconds
     * since EPOCH, or null if it could not be parsed.
     */
    private Long trySeconds(String str) {
        for(String pattern : datePatterns) {
            SimpleDateFormat parser = new SimpleDateFormat(pattern, locale);
            try {
                long milliseconds = parser.parse(str).getTime();
                return milliseconds / 1000L;
            }
            catch(Exception e) {
                // Just ignore and try the next pattern in `datePatterns`.
            }
        }
        // Could not parse the string into a meaningful date, return null.
        return null;
    }

    public static void addDatePattern(String pattern) {

        if(pattern == null) {
            throw new NullPointerException("date-pattern cannot be null");
        }

        datePatterns.add(pattern);
    }

    public static String parseAndRender(String template) {
        Template t = Template.parse(template);
        return t.render();
    }

    public interface CustomDateFormatSupport<T> {
        Long getAsSeconds(T value);

        boolean support(Object in);
    }

    public static Filter withCustomDateType(CustomDateFormatSupport typeSupport) {
        return new Date(typeSupport);
    }
}

