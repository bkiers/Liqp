package liqp.filters.date;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FullDateFromTemporalProducer implements BiFunction<TemporalAccessor, ZoneId, ZonedDateTime> {

    /**
     * Follow ruby rules: if some datetime part is missing, the default is taken from `now` with
     * default zone
     */
    @SuppressWarnings({"java:S3776", "java:S1872"})
    @Override
    public ZonedDateTime apply(TemporalAccessor temporal, ZoneId defaultZone) {
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
            zoneId = defaultZone;
        }

        final LocalDateTime now = LocalDateTime.now(zoneId);

        TemporalField[] copyThese = new TemporalField[]{
                NANO_OF_SECOND,
                SECOND_OF_MINUTE,
                MINUTE_OF_HOUR,
                HOUR_OF_DAY,
                DAY_OF_MONTH,
                MONTH_OF_YEAR,
                YEAR,
        };


        if ("java.time.format.Parsed".equals(temporal.getClass().getName())) {
            Map<TemporalField, Function<TemporalAccessor, LocalDateTime>> factories = new HashMap<>();
            factories.put(DAY_OF_WEEK, t -> now.with(TemporalAdjusters.previousOrSame(DayOfWeek.from(t))));
            TemporalAccessor onlyField = onlyField(temporal, factories, copyThese);
            if (onlyField != null) {
                return apply(onlyField, zoneId);
            }
        }

        LocalDateTime res = now;
        boolean zeroField = true;
        for (TemporalField tf: copyThese) {
            if (zeroField && temporal.isSupported(tf)) {
                zeroField = false;
            }
            if (zeroField) {
                if (temporal.isSupported(tf)) {
                    long minimum = temporal.range(tf).getMinimum();
                    res = res.with(tf, minimum);
                } else {
                    res = res.with(tf, tf.range().getMinimum());
                }
            } else {
                if (temporal.isSupported(tf)) {
                    res = res.with(tf, temporal.get(tf));
                } else {
                    res = res.with(tf, now.get(tf));
                }
            }
        }
        return res.atZone(zoneId);
    }

    private static TemporalAccessor onlyField(TemporalAccessor temporal, Map<TemporalField, Function<TemporalAccessor, LocalDateTime>> factories,
            TemporalField[] butThese) {
        if (factories == null || factories.isEmpty()) {
            return null;
        }
        for (TemporalField tf: butThese) {
            if (temporal.isSupported(tf)) {
                return null;
            }
        }
        for (Map.Entry<TemporalField, Function<TemporalAccessor, LocalDateTime>> entry: factories.entrySet()) {
            if (temporal.isSupported(entry.getKey())) {
                return entry.getValue().apply(temporal);
            }
        }
        return null;
    }
}
