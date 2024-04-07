package liqp.filters.date;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoField.INSTANT_SECONDS;

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

        datePatterns.add("EEE MMM dd hh:mm:ss yyyy");
        datePatterns.add("EEE MMM dd hh:mm yyyy");
        datePatterns.add("yyyy-MM-dd");
        datePatterns.add("dd-MM-yyyy");

        // this is section without `T`, change here and do same change in section below with `T`
        datePatterns.add("yyyy-MM-dd HH:mm");
        datePatterns.add("yyyy-MM-dd HH:mm X");
        datePatterns.add("yyyy-MM-dd HH:mm Z");
        datePatterns.add("yyyy-MM-dd HH:mm z");
        datePatterns.add("yyyy-MM-dd HH:mm'Z'");

        datePatterns.add("yyyy-MM-dd HH:mm:ss");
        datePatterns.add("yyyy-MM-dd HH:mm:ss X");
        datePatterns.add("yyyy-MM-dd HH:mm:ss Z");
        datePatterns.add("yyyy-MM-dd HH:mm:ss z");
        datePatterns.add("yyyy-MM-dd HH:mm:ss'Z'");

        datePatterns.add("yyyy-MM-dd HH:mm:ss.SSS");
        datePatterns.add("yyyy-MM-dd HH:mm:ss.SSS X");
        datePatterns.add("yyyy-MM-dd HH:mm:ss.SSS Z");
        datePatterns.add("yyyy-MM-dd HH:mm:ss.SSS z");
        datePatterns.add("yyyy-MM-dd HH:mm:ss.SSS'Z'");

        datePatterns.add("yyyy-MM-dd HH:mm:ss.SSSSSS");
        datePatterns.add("yyyy-MM-dd HH:mm:ss.SSSSSS X");
        datePatterns.add("yyyy-MM-dd HH:mm:ss.SSSSSS Z");
        datePatterns.add("yyyy-MM-dd HH:mm:ss.SSSSSS z");
        datePatterns.add("yyyy-MM-dd HH:mm:ss.SSSSSS'Z'");

        datePatterns.add("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
        datePatterns.add("yyyy-MM-dd HH:mm:ss.SSSSSSSSS X");
        datePatterns.add("yyyy-MM-dd HH:mm:ss.SSSSSSSSS Z");
        datePatterns.add("yyyy-MM-dd HH:mm:ss.SSSSSSSSS z");
        datePatterns.add("yyyy-MM-dd HH:mm:ss.SSSSSSSSS'Z'");

        // this is section with `T`
        datePatterns.add("yyyy-MM-dd'T'HH:mm");
        datePatterns.add("yyyy-MM-dd'T'HH:mm X");
        datePatterns.add("yyyy-MM-dd'T'HH:mm Z");
        datePatterns.add("yyyy-MM-dd'T'HH:mm z");
        datePatterns.add("yyyy-MM-dd'T'HH:mm'Z'");

        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss X");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss Z");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss z");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss'Z'");

        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSS");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSS X");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSS z");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSS X");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSS Z");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSS z");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS X");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS Z");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS z");
        datePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'");

    }

    public static ZonedDateTime parse(String str, Locale locale, ZoneId defaultZone) {

        for(String pattern : datePatterns) {
            try {

                DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern(pattern)
                        .toFormatter(locale);

                TemporalAccessor temporalAccessor = timeFormatter.parse(str);
                return getZonedDateTimeFromTemporalAccessor(temporalAccessor, defaultZone);
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
    public static ZonedDateTime getZonedDateTimeFromTemporalAccessor(TemporalAccessor temporal, ZoneId defaultZone) {
        if (temporal == null) {
            return ZonedDateTime.now(defaultZone);
        }
        if (temporal instanceof ZonedDateTime) {
            return (ZonedDateTime) temporal;
        }
        if (temporal instanceof Instant) {
            return ZonedDateTime.ofInstant((Instant) temporal, defaultZone);
        }

        ZoneId zoneId = temporal.query(TemporalQueries.zone());
        if (zoneId == null) {
            LocalDate date = temporal.query(TemporalQueries.localDate());
            LocalTime time = temporal.query(TemporalQueries.localTime());

            if (date == null) {
                date = LocalDate.now(defaultZone);
            }
            if (time == null) {
                time = LocalTime.now(defaultZone);
            }
            return ZonedDateTime.of(date, time, defaultZone);
        } else {
            LocalDateTime now = LocalDateTime.now(zoneId);
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
                if (temporal.isSupported(tf)) {
                    now = now.with(tf, temporal.get(tf));
                }
            }
            return now.atZone(zoneId);
        }
    }
}
