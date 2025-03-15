package liqp.filters.date;

import static java.time.temporal.ChronoField.YEAR;

import java.text.DateFormatSymbols;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;

public abstract class BasicDateParser {

    // Since Liquid supports dates like `March 1st`, this list will
    // hold strings that will be removed from the input string.
    private static final Map<String, String> toBeReplaced = new LinkedHashMap<>();
    static {
        toBeReplaced.put("11th", "11");
        toBeReplaced.put("12th", "12");
        toBeReplaced.put("13th", "13");
        toBeReplaced.put("1st", "1");
        toBeReplaced.put("2nd", "2");
        toBeReplaced.put("3rd", "3");
        toBeReplaced.put("4th", "4");
        toBeReplaced.put("5th", "5");
        toBeReplaced.put("6th", "6");
        toBeReplaced.put("7th", "7");
        toBeReplaced.put("8th", "8");
        toBeReplaced.put("9th", "9");
        toBeReplaced.put("0th", "0");
    }

    public static String removeSequentialSuffixes(String input) {
        for (Map.Entry<String, String> entry : toBeReplaced.entrySet()) {
            input = input.replaceAll("(?i)"+entry.getKey(), entry.getValue());
        }
        return input;
    }


    protected final List<String> cachedPatterns = new CopyOnWriteArrayList<>();
    private final ArrayToRegexp arrayToRegexp = new ArrayToRegexp();
    protected final BiFunction<TemporalAccessor, ZoneId, ZonedDateTime> fullDateFromTemporalProducer = new FullDateFromTemporalProducer();

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
                return fullDateFromTemporalProducer.apply(temporalAccessor, defaultZone);
            } catch (Exception e) {
                // ignore
            }
        }
        // Could not parse the string into a meaningful date, return null.
        return null;
    }

    protected TemporalAccessor parseUsingPattern(String normalizedInput, String patternToMatch, Locale locale) {
        if (dayOfWeekIsRedundant(patternToMatch)) {
            patternToMatch = patternToMatch.replace("EEEE", "");
            patternToMatch = patternToMatch.replace("EEE", "");
            normalizedInput = normalizedInput.replaceAll("(?i)" + getFullDaysOfWeekForReplaceRegexp(locale), "");
            normalizedInput = normalizedInput.replaceAll("(?i)" + getShortDaysOfWeekForReplaceRegexp(locale), "");
        }

        DateTimeFormatter timeFormatter;
        if (isTwoDigitYear(patternToMatch)) {
            timeFormatter = withTwoDigitYearWithBase2000(patternToMatch, locale);
        } else {
            timeFormatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern(patternToMatch)
                    .toFormatter(locale);
        }

        return timeFormatter.parse(normalizedInput);
    }

    private String getShortDaysOfWeekForReplaceRegexp(Locale locale) {
        return arrayToRegexp.apply(new DateFormatSymbols(locale).getShortWeekdays(), locale);
    }

    private String getFullDaysOfWeekForReplaceRegexp(Locale locale) {
        return arrayToRegexp.apply(new DateFormatSymbols(locale).getWeekdays(), locale);
    }

    private DateTimeFormatter withTwoDigitYearWithBase2000(String patternToMatch, Locale locale) {
        String[] partsAroundYear;
        if (patternToMatch.contains("yy")) {
            partsAroundYear = patternToMatch.split("yy");
        } else {
            partsAroundYear = patternToMatch.split("y");
        }
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder()
                .parseCaseInsensitive();
        if (partsAroundYear.length > 0 && !partsAroundYear[0].isEmpty()) {
            builder.appendPattern(partsAroundYear[0]);
        }
        if (!patternToMatch.contains("GG")) {
            builder.appendValueReduced(YEAR, 2, 2, 2000);
        } else {
            builder.appendValue(YEAR);
        }
        if (partsAroundYear.length > 1 && !partsAroundYear[1].isEmpty()) {
            builder.appendPattern(partsAroundYear[1]);
        }
        return builder.toFormatter(locale);
    }

    private static boolean isTwoDigitYear(String patternToMatch) {
        return  (patternToMatch.contains("yy") || patternToMatch.contains("y"))
                &&
                !patternToMatch.contains("yyyy");
    }

    private static boolean dayOfWeekIsRedundant(String pattern) {
        return (pattern.contains("y"))
                && pattern.contains("M")
                && pattern.contains("d")
                && pattern.contains("EEE");
    }

}
