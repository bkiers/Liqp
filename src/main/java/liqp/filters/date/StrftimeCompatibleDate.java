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
    private final String zoneId;

    // todo: to be removed
    public static void main(String[] args) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("Z", Locale.ENGLISH);
        Date res = sdf.parse("-0500");
        System.out.println("date: " + res);
        System.out.println("tz:" + sdf.getTimeZone().getID());

//
//        //
//
//        // https://stackoverflow.com/questions/19854768/how-to-implement-a-custom-date-format-in-java
//        // https://docs.oracle.com/javase/7/docs/api/java/text/ChoiceFormat.html
//        // https://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html
//
//        // z - General time zone: Pacific Standard Time; PST; GMT-08:00
//        // Z - RFC 822 time zone: -0800
//        // X - ISO 8601 time zone: -08; -0800; -08:00
//
//        // ruby default time format:
//        // https://www.tutorialspoint.com/ruby/ruby_date_time.htm
//        // also test this: 2021-01-25 15:19:44 +0200
//        // general liquid info:
//        // https://shopify.github.io/liquid/filters/date/
//
//        // tester: http://strftime.net/
//
//        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
//        res = sdf.parse("2020-11-03 16:40:44 Pacific Standard Time");
//
//        System.out.println(res);
//        System.out.println("tz:" + sdf.getTimeZone().getID());
//
//        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzzz");
//        res = sdf.parse("2020-11-03 16:40:44 Pacific Standard Time");
//
//        System.out.println(res);
//        System.out.println("tz:" + sdf.getTimeZone().getID());
//
//        sdf = new SimpleDateFormat("z");
//        sdf.setTimeZone(TimeZone.getTimeZone("PST"));
//        System.out.println("tz parsed: " + sdf.format(res));
//
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(res);
//        System.out.println(cal);
//        TimeZone timeZone = cal.getTimeZone();
//        System.out.println("tz: " + timeZone);
//        res = new SimpleDateFormat("yyyy-MM-dd HH:mm:s")
//                .parse("2011-23-03 16:40:44");
//        Calendar cal2 = Calendar.getInstance();
//        cal2.setTime(res);
//        timeZone = cal2.getTimeZone();
//        System.out.println("tz from empty: " + timeZone);
    }

    /**
     * In case if anyone interesting about full set
     * of supported by ruby date patterns:
     * there no such set as the parsing there happening based on
     * heuristic algorithms.
     * This is how it looks like(~3K lines just for date parse):
     * https://github.com/ruby/ruby/blob/ee102de6d7ec2454dc5da223483737478eb7bcc7/ext/date/date_parse.c
     */
    public static Set<String> datePatterns = new HashSet<String>();

    static {
        datePatterns.add("yyyy-MM-dd HH:mm:ss");
        datePatterns.add("yyyy-MM-dd HH:mm:ss Z");
        datePatterns.add("EEE MMM dd hh:mm:ss yyyy");
        // 2010-10-31 00:00:00 -0500
    }


    private final long date;

    /**
     * Terrible fact:
     * new Date() - return timezone with taking to account the (default) timezone offset
     * @param milliseconds - the milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public StrftimeCompatibleDate(long milliseconds, TimeZone timeZone) {
        this.date = milliseconds;
        this.zoneId = safeZoneId(timeZone);
    }

    private String safeZoneId(TimeZone id) {
        return id == null ? null : id.getID();
    }

    public StrftimeCompatibleDate(TimeZone timeZone) {
        this.zoneId = safeZoneId(timeZone);
        this.date = 0L;
    }

    public StrftimeCompatibleDate() {
        this.zoneId = TimeZone.getDefault().getID();
        this.date = 0L;
    }

    public StrftimeCompatibleDate(long milliseconds) {
        this.zoneId = TimeZone.getDefault().getID();
        this.date = milliseconds;
    }
    public static StrftimeCompatibleDate parse(String str, Locale locale) {

        for(String pattern : datePatterns) {

            SimpleDateFormat parser = new SimpleDateFormat(pattern, locale);

            try {
                Date parse = parser.parse(str);
                return new StrftimeCompatibleDate(parse.getTime(), parser.getTimeZone());
            }
            catch(Exception e) {
                // Just ignore and try the next pattern in `datePatterns`.
            }
        }

        // Could not parse the string into a meaningful date, return null.
        return null;
    }

    public Long getDate() {
        return date;
    }

    public String getZoneId() {
        return zoneId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StrftimeCompatibleDate{");
        sb.append("zoneId='").append(zoneId).append('\'');
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }
}
