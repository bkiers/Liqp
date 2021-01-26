package liqp.filters.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class StrftimeCompatibleDate {

    // todo: to be removed
    public static void main(String[] args) throws ParseException {
        // https://stackoverflow.com/questions/19854768/how-to-implement-a-custom-date-format-in-java
        // https://docs.oracle.com/javase/7/docs/api/java/text/ChoiceFormat.html
        // https://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html

        // z - General time zone: Pacific Standard Time; PST; GMT-08:00
        // Z - RFC 822 time zone: -0800
        // X - ISO 8601 time zone: -08; -0800; -08:00

        // ruby default time format:
        // https://www.tutorialspoint.com/ruby/ruby_date_time.htm
        // also test this: 2021-01-25 15:19:44 +0200
        // general liquid info:
        // https://shopify.github.io/liquid/filters/date/

        // tester: http://strftime.net/

        Date res = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")
                .parse("2011-23-03 16:40:44 GMT");
        System.out.println(res);

        Calendar cal = Calendar.getInstance();
        cal.setTime(res);
        System.out.println(cal);
        TimeZone timeZone = cal.getTimeZone();

    }
    public static Set<String> datePatterns = new HashSet<String>();

    static {
        datePatterns.add("yyyy-MM-dd HH:mm:ss");
        datePatterns.add("EEE MMM dd hh:mm:ss yyyy");
    }


    private final Date date;

    /**
     * @param milliseconds - the milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public StrftimeCompatibleDate(long milliseconds) {
        this.date = new Date(milliseconds);
    }
    public StrftimeCompatibleDate() {
        this.date = new Date();
    }

    public static StrftimeCompatibleDate parse(String str, Locale locale) {

        for(String pattern : datePatterns) {

            SimpleDateFormat parser = new SimpleDateFormat(pattern, locale);

            try {
                long milliseconds = parser.parse(str).getTime();
                return new StrftimeCompatibleDate(milliseconds);
            }
            catch(Exception e) {
                // Just ignore and try the next pattern in `datePatterns`.
            }
        }

        // Could not parse the string into a meaningful date, return null.
        return null;
    }

    public Date getDate() {
        return date;
    }
}
