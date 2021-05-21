package liqp.filters.date;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

public class Parser {

    /**
     * In case if anyone interesting about full set
     * of supported by ruby date patterns:
     * there no such set as the parsing there happening based on
     * heuristic algorithms.
     * This is how it looks like(~3K lines just for date parse):
     * https://github.com/ruby/ruby/blob/ee102de6d7ec2454dc5da223483737478eb7bcc7/ext/date/date_parse.c
     *
     * And here's python.
     * Just an example how it is violating standard in details regarding timezone representation:
     * https://docs.python.org/3/library/datetime.html#strftime-and-strptime-behavior
     */
    public static List<String> datePatterns = new ArrayList<>();

    static {
        datePatterns.add("yyyy-MM-dd HH:mm:ss");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss");
        datePatterns.add("yyyy-MM-dd HH:mm:ss Z");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss Z");
        datePatterns.add("yyyy-MM-dd HH:mm:ss X");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss X");
        datePatterns.add("yyyy-MM-dd HH:mm:ss z");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss z");
        datePatterns.add("EEE MMM dd hh:mm:ss yyyy");
    }

    public static ZonedDateTime parse(String str, Locale locale) {

        for(String pattern : datePatterns) {
            try {

                DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern(pattern)
                        .toFormatter(locale);

                TemporalAccessor temporalAccessor = timeFormatter.parse(str);
                return getZonedDateTimeFromTemporalAccessor(temporalAccessor);
            } catch (Exception e) {
                // ignore
            }
        }

        // Could not parse the string into a meaningful date, return null.
        return null;
    }

    /**
     * Follow ruby rules: if some datetime part is missing,
     * the default is taken from `now` with default zone
     */
    public static ZonedDateTime getZonedDateTimeFromTemporalAccessor(TemporalAccessor temporalAccessor) {
        if (temporalAccessor instanceof ZonedDateTime) {
            return (ZonedDateTime) temporalAccessor;
        }
        LocalDateTime now = LocalDateTime.now();
        TemporalField[] copyThese = new TemporalField[]{
                YEAR,
                MONTH_OF_YEAR,
                DAY_OF_MONTH,
                HOUR_OF_DAY,
                MINUTE_OF_HOUR,
                SECOND_OF_MINUTE,
                NANO_OF_SECOND
        };
        for (TemporalField tf: copyThese) {
            if (temporalAccessor.isSupported(tf)) {
                now = now.with(tf, temporalAccessor.get(tf));
            }
        }

        ZoneId zoneId = temporalAccessor.query(TemporalQueries.zone());
        if (zoneId == null) {
            zoneId = ZoneId.systemDefault();
        }

        return now.atZone(zoneId);
    }
}
