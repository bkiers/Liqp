package liqp.filters.date;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Reformat a date
 *
 * %a - The abbreviated weekday name (``Sun'')
 * %A - The  full  weekday  name (``Sunday'')
 * %b - The abbreviated month name (``Jan'')
 * %B - The  full  month  name (``January'')
 * %c - The preferred local date and time representation
 * %d - Day of the month (01..31)
 * %e - Day of the month (1..31)
 * %H - Hour of the day, 24-hour clock (00..23)
 * %I - Hour of the day, 12-hour clock (01..12)
 * %j - Day of the year (001..366)
 * %k - Hour of the day, 24-hour clock (0..23)
 * %l - Hour of the day, 12-hour clock (0..12)
 * %m - Month of the year (01..12)
 * %M - Minute of the hour (00..59)
 * %p - Meridian indicator (``AM''  or  ``PM'')
 * %S - Second of the minute (00..60)
 * %U - Week  number  of the current year,
 *      starting with the first Sunday as the first
 *      day of the first week (00..53)
 * %W - Week  number  of the current year,
 *      starting with the first Monday as the first
 *      day of the first week (00..53)
 * %w - Day of the week (Sunday is 0, 0..6)
 * %x - Preferred representation for the date alone, no time
 * %X - Preferred representation for the time alone, no date
 * %y - Year without a century (00..99)
 * %Y - Year with century
 * %Z - Time zone name
 * %% - Literal ``%'' character
 */
public class StrftimeDateFormatter {

    /**
     * Underlying SimpleDateFormat are not thread-safe, so lets each thread work with ouw copy!
     * This became critical, as the only way to get timezone from formatted date in java7 is to
     * use mutable {@link SimpleDateFormat#parse(String)} method.
     */
    private static final ThreadLocal<Map<Locale, StrftimeDateFormatter>> localeMapHolder = ThreadLocal.withInitial(HashMap::new);

    public static StrftimeDateFormatter getInstance(Locale locale) {
        Map<Locale, StrftimeDateFormatter> localeMap = localeMapHolder.get();
        if (!localeMap.containsKey(locale)) {
            localeMap.put(locale, new StrftimeDateFormatter(locale));
        }
        return localeMap.get(locale);
    }

    private final Locale locale;
    private final Map<Character, DateTimeFormatter> LIQUID_TO_JAVA_FORMAT = new HashMap<>();

    private StrftimeDateFormatter(Locale locale) {
        this.locale = locale;
        init();
    }

    private void init() {

        // %% - Literal ``%'' character
        LIQUID_TO_JAVA_FORMAT.put('%', DateTimeFormatter.ofPattern("%", locale));

        // %a - The abbreviated weekday name (``Sun'')
        LIQUID_TO_JAVA_FORMAT.put('a', DateTimeFormatter.ofPattern("EEE", locale));

        // %A - The  full  weekday  name (``Sunday'')
        LIQUID_TO_JAVA_FORMAT.put('A', DateTimeFormatter.ofPattern("EEEE", locale));

        // %b - The abbreviated month name (``Jan'')
        LIQUID_TO_JAVA_FORMAT.put('b', DateTimeFormatter.ofPattern("MMM", locale));
        LIQUID_TO_JAVA_FORMAT.put('h', DateTimeFormatter.ofPattern("MMM", locale));

        // %B - The  full  month  name (``January'')
        LIQUID_TO_JAVA_FORMAT.put('B', DateTimeFormatter.ofPattern("MMMM", locale));

        // %c - The preferred local date and time representation
        LIQUID_TO_JAVA_FORMAT.put('c', DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy", locale));

        // %d - Day of the month (01..31)
        LIQUID_TO_JAVA_FORMAT.put('d', DateTimeFormatter.ofPattern("dd", locale));

        // %e - Day of the month (1..31)
        LIQUID_TO_JAVA_FORMAT.put('e', DateTimeFormatter.ofPattern("d", locale));

        // %H - Hour of the day, 24-hour clock (00..23)
        LIQUID_TO_JAVA_FORMAT.put('H', DateTimeFormatter.ofPattern("HH", locale));

        // %I - Hour of the day, 12-hour clock (01..12)
        LIQUID_TO_JAVA_FORMAT.put('I', DateTimeFormatter.ofPattern("hh", locale));

        // %j - Day of the year (001..366)
        LIQUID_TO_JAVA_FORMAT.put('j', DateTimeFormatter.ofPattern("DDD", locale));

        // %k - Hour of the day, 24-hour clock (0..23)
        LIQUID_TO_JAVA_FORMAT.put('k', DateTimeFormatter.ofPattern("H", locale));

        // %l - Hour of the day, 12-hour clock (1..12)
        LIQUID_TO_JAVA_FORMAT.put('l', DateTimeFormatter.ofPattern("h", locale));

        // %m - Month of the year (01..12)
        LIQUID_TO_JAVA_FORMAT.put('m', DateTimeFormatter.ofPattern("MM", locale));

        // %M - Minute of the hour (00..59)
        LIQUID_TO_JAVA_FORMAT.put('M', DateTimeFormatter.ofPattern("mm", locale));

        // %p - Meridian indicator (``AM''  or  ``PM'')
        LIQUID_TO_JAVA_FORMAT.put('p', DateTimeFormatter.ofPattern("a", locale));

        // %S - Second of the minute (00..60)
        LIQUID_TO_JAVA_FORMAT.put('S', DateTimeFormatter.ofPattern("ss", locale));

        // %U - Week  number  of the current year,
        //      starting with the first Sunday as the first
        //      day of the first week (00..53)
        LIQUID_TO_JAVA_FORMAT.put('U', DateTimeFormatter.ofPattern("ww", locale));

        // %W - Week  number  of the current year,
        //      starting with the first Monday as the first
        //      day of the first week (00..53)
        LIQUID_TO_JAVA_FORMAT.put('W', DateTimeFormatter.ofPattern("ww", locale));

        // %w - Day of the week (Sunday is 0, 0..6)
        LIQUID_TO_JAVA_FORMAT.put('w', DateTimeFormatter.ofPattern("F", locale));

        // %x - Preferred representation for the date alone, no time
        LIQUID_TO_JAVA_FORMAT.put('x', DateTimeFormatter.ofPattern("MM/dd/yy", locale));

        // %X - Preferred representation for the time alone, no date
        LIQUID_TO_JAVA_FORMAT.put('X', DateTimeFormatter.ofPattern("HH:mm:ss", locale));

        // %y - Year without a century (00..99)
        LIQUID_TO_JAVA_FORMAT.put('y', DateTimeFormatter.ofPattern("yy", locale));

        // %Y - Year with century
        LIQUID_TO_JAVA_FORMAT.put('Y', DateTimeFormatter.ofPattern("yyyy", locale));

        // %Z - Time zone name
        LIQUID_TO_JAVA_FORMAT.put('Z', DateTimeFormatter.ofPattern("zzzz", locale));

        // %z - Time zone offset
        LIQUID_TO_JAVA_FORMAT.put('z',DateTimeFormatter.ofPattern("XXXX", locale));

    }

    public String format(String format, ZonedDateTime date) {

        // todo: check null value handling in ruby
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < format.length(); i++) {

            char ch = format.charAt(i);

            if (ch == '%') {

                i++;

                if (i == format.length()) {
                    // a trailing (single) '%' sign: just append it
                    builder.append("%");
                    break;
                }

                char next = format.charAt(i);

                DateTimeFormatter javaFormat = LIQUID_TO_JAVA_FORMAT.get(next);

                if (javaFormat == null) {
                    // no valid date-format: append the '%' and the 'next'-char
                    builder.append("%").append(next);
                } else {
                    builder.append(javaFormat.format(date));
                }
            } else {
                builder.append(ch);
            }
        }

        return builder.toString();
    }
}
