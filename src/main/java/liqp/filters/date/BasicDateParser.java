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

public abstract class BasicDateParser {

    private final List<String> cachedPatterns = new ArrayList<>();

    protected BasicDateParser() {

    }

    protected BasicDateParser(List<String> patterns) {
        cachedPatterns.addAll(patterns);
    }

    protected void storePattern(String pattern) {
        cachedPatterns.add(pattern);
    }

    public abstract ZonedDateTime parse(String valAsString, Locale locale, ZoneId timeZone);

    protected ZonedDateTime parseUsingCachedPatterns(String str, Locale locale, ZoneId defaultZone) {
        for(String pattern : cachedPatterns) {
            try {
                TemporalAccessor temporalAccessor = parseUsingPattern(str, pattern, locale);
                return getZonedDateTimeFromTemporalAccessor(temporalAccessor, defaultZone);
            } catch (Exception e) {
                // ignore
            }
        }
        // Could not parse the string into a meaningful date, return null.
        return null;
    }

    protected TemporalAccessor parseUsingPattern(String normalized, String pattern, Locale locale) {
        DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(locale);

        return timeFormatter.parse(normalized);
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
