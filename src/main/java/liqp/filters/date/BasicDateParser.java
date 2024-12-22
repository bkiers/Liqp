package liqp.filters.date;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import static java.time.temporal.ChronoField.*;

public abstract class BasicDateParser {

    // Since Liquid supports dates like `March 1st`, this list will
    // hold strings that will be removed from the input string.
    private static final Map<String, String> toBeReplaced = new LinkedHashMap<String, String>() {{
        this.put("11th", "11");
        this.put("12th", "12");
        this.put("13th", "13");
        this.put("1st", "1");
        this.put("2nd", "2");
        this.put("3rd", "3");
        this.put("4th", "4");
        this.put("5th", "5");
        this.put("6th", "6");
        this.put("7th", "7");
        this.put("8th", "8");
        this.put("9th", "9");
        this.put("0th", "0");
    }};
    protected String removeSequentialSuffixes(String input) {
        for (Map.Entry<String, String> entry : toBeReplaced.entrySet()) {
            input = input.replaceAll("(?i)"+entry.getKey(), entry.getValue());
        }
        return input;
    }


    protected final List<String> cachedPatterns = new CopyOnWriteArrayList<>();

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
                return getFullDateIfPossible(temporalAccessor, defaultZone);
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
     * Follow ruby rules: if some datetime part is missing, the default is taken from `now` with
     * default zone
     */
    public static ZonedDateTime getFullDateIfPossible(TemporalAccessor temporal, ZoneId defaultZone) {
        if (temporal == null) {
            return ZonedDateTime.now(defaultZone);
        }
        if (temporal instanceof ZonedDateTime) {
            return (ZonedDateTime) temporal;
        }
        if (temporal instanceof Instant) {
            return ZonedDateTime.ofInstant((Instant) temporal, defaultZone);
        }
        TemporalField[] copyThese = new TemporalField[]{
                YEAR,
                MONTH_OF_YEAR,
                DAY_OF_MONTH,
                HOUR_OF_DAY,
                MINUTE_OF_HOUR,
                SECOND_OF_MINUTE,
                NANO_OF_SECOND
        };


        ZoneId zoneId = temporal.query(TemporalQueries.zone());
        if (zoneId == null) {
            zoneId = defaultZone;
        }

        final LocalDateTime now = LocalDateTime.now(zoneId);

        if ("java.time.format.Parsed".equals(temporal.getClass().getName())) {
            Map<TemporalField, Function<TemporalAccessor, LocalDateTime>> factories = new HashMap<>();
            factories.put(DAY_OF_WEEK, t -> now.with(TemporalAdjusters.previousOrSame(DayOfWeek.from(t))));
            TemporalAccessor onlyField = onlyField(temporal, factories, copyThese);
            if (onlyField != null) {
                return getFullDateIfPossible(onlyField, zoneId);
            }
        }


        LocalDateTime res = now.with(TemporalAdjusters.ofDateAdjuster(date -> date));
        for (TemporalField tf: copyThese) {
            if (temporal.isSupported(tf)) {
                res = res.with(tf, temporal.get(tf));
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
