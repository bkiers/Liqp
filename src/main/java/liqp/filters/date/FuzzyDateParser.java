package liqp.filters.date;

import static liqp.LValue.isBlank;

import java.text.DateFormatSymbols;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class is writen for fun.
 * Even if it may be extended to be very powerful, this is not intention. For example, it may
 * use locale information to guess some pattern parts,
 * like "leading zeros appear to be less commonly used in Germany than in Austria and Switzerland".
 * But it is not implemented. And not going to be.
 * It simply must cover all possible cases of format that been used in previous
 * implementation of BasicDateParser. Which in fact is quite poor yet did the work well.
 * So not expect super-magic here.
 * And yes.
 * It is called "fuzzy" but is having strict and well-defined rules of "guessing".
 * So this class is quite exact. See test for details.
 */
public class FuzzyDateParser extends BasicDateParser {

    public static boolean CLDR_LOADED = new DateFormatSymbols(Locale.GERMANY)
            .getShortMonths()[1]
            .endsWith(".");

    @Override
    public ZonedDateTime parse(String valAsString, Locale locale, ZoneId defaultZone) {
        String normalized = valAsString.toLowerCase();
        ZonedDateTime zonedDateTime = parseUsingCachedPatterns(normalized, locale, defaultZone);
        if (zonedDateTime != null) {
            return zonedDateTime;
        }

        String pattern = guessPattern(normalized, locale);

        TemporalAccessor temporalAccessor = parseUsingPattern(normalized, pattern, locale);
        if (temporalAccessor == null) {
            return null;
        }
        storePattern(pattern);
        return getZonedDateTimeFromTemporalAccessor(temporalAccessor, defaultZone);
    }

    String guessPattern(String normalized, Locale locale) {
        List<Part> parts = new ArrayList<>();
        // we start as one big single unparsed part
        DateParseContext ctx = new DateParseContext(locale);
        parts.add(new UnparsedPart(0, normalized.length(), normalized));

        while (haveUnparsed(parts)) {
            parts = parsePart(parts, ctx);
        }

        return reconstructPattern(parts);
    }

    private List<Part> parsePart(List<Part> parts, DateParseContext ctx) {

        if (notSet(ctx.hasYear)) {
            LookupResult result = lookup(parts, yearWithEraExtractor);
            if (result.found) {
                ctx.hasYear = true;
                return result.parts;
            }
        }
        if (notSet(ctx.hasTime)) {
            LookupResult result = lookup(parts, regularTimeExtractor);
            if (result.found) {
                ctx.hasTime = true;
                return result.parts;
            }
            ctx.hasTime = false;
        }

        if (notSet(ctx.hasYear)) {
            LookupResult result = lookup(parts, plainYearExtractor);
            if (result.found) {
                ctx.hasYear = true;
                return result.parts;
            }
            // last "year check" and since we are here - there is no year
            ctx.hasYear = false;
        }
        if (notSet(ctx.hasMonthName)) {
            LookupResult result = lookup(parts, fullMonthExtractor(ctx.locale));
            if (result.found) {
                ctx.hasMonthName = true;
                return result.parts;
            }

            result = lookup(parts, shortMonthExtractor(ctx.locale));
            if (result.found) {
                ctx.hasMonthName = true;
                return result.parts;
            }

            ctx.hasMonthName = false;
        }

        return markAsUnrecognized(parts);
    }



    private String reconstructPattern(List<Part> parts) {
        return parts.stream().map(p -> {
            if (p.state() == PartState.PARSED) {
                return ((ParsedPart) p).getPattern();
            } else {
                return p.source();
            }
        }).collect(Collectors.joining());
    }

    static class DateParseContext {

        private final Locale locale;
        Boolean hasYear;
        Boolean hasMonthName;
        Boolean hasTime;

        public DateParseContext(Locale locale) {
            this.locale = locale;
        }
    }
    static class PartExtractorResult {
        public PartExtractorResult(){}
        public PartExtractorResult(String formatterPattern){
            this.formatterPattern = formatterPattern;
        }
        boolean found;
        int start;
        int end;
        String formatterPattern;
    }
    interface PartExtractor {
        PartExtractorResult extract(String source);
    }
    static class RegexPartExtractor implements PartExtractor {
        protected final Pattern pattern;
        protected final String formatterPattern;

        RegexPartExtractor(String regex, String formatterPattern) {
            this.pattern = Pattern.compile(regex);
            this.formatterPattern = formatterPattern;
        }

        @Override
        public PartExtractorResult extract(String source) {
            Matcher matcher = pattern.matcher(source);
            if (matcher.find()) {
                PartExtractorResult result = new PartExtractorResult(formatterPattern);
                result.found = true;
                result.start = matcher.start(1);
                result.end = matcher.end(1);
                return result;
            }
            return new PartExtractorResult();
        }
    }
    static class YearWithEra extends RegexPartExtractor {
        YearWithEra() {
            super("(?:^|.*?\\D)(?<year>\\d+)(?<eraSeparator>\\s*)(?<era>AD|BC|Anno Domini|Before Christ)(?:$|\\D.*?)", null);
        }

        @Override
        public PartExtractorResult extract(String source) {
            Matcher matcher = pattern.matcher(source);
            if (matcher.find()) {
                PartExtractorResult result = new PartExtractorResult();
                result.found = true;
                result.start = matcher.start("year");
                result.formatterPattern = "yyyy";
                String era = matcher.group("era");
                if(!isBlank(era)) {
                    String eraSeparator = matcher.group("eraSeparator");
                    if (eraSeparator != null) {
                        result.formatterPattern += eraSeparator;
                    }
                    result.end = matcher.end("era");
                    if (era.length() == 2) {
                        result.formatterPattern += "GG";
                    } else {
                        result.formatterPattern += "GGGG";
                    }
                } else {
                    result.end = matcher.end("year");
                }
                return result;
            }
            return new PartExtractorResult();
        }
    }
    PartExtractor yearWithEraExtractor = new YearWithEra();

    PartExtractor plainYearExtractor = new RegexPartExtractor(".*\\b?(\\d{4})\\b?.*", "yyyy");

    static class PartExtractorDelegate implements PartExtractor {
        private PartExtractor delegate;

        @Override
        public PartExtractorResult extract(String source) {
            return delegate.extract(source);
        }
    }
    static class FullMonthExtractor extends PartExtractorDelegate {
        public FullMonthExtractor(Locale locale, String formatterPattern) {
            if (locale == null || Locale.ROOT.equals(locale)) {
                locale = Locale.US;
            }
            String[] months = withoutNulls(getMonthsNamesFromLocale(locale));
            String monthPattern = String.join("|", months);
            super.delegate = new RegexPartExtractor(".*\\b?(" + monthPattern + ")\\b?.*", formatterPattern);
        }

        protected String[] getMonthsNamesFromLocale(Locale locale) {
            return new DateFormatSymbols(locale).getMonths();
        }

        protected String[] withoutNulls(String[] shortMonths) {
            return Arrays.stream(shortMonths)
                    .filter(month -> month != null && !month.isEmpty())
                    .map(Pattern::quote)
                    .map(this::convertMonthName)
                    .toArray(String[]::new);
        }
        protected String convertMonthName(String monthName) {
            return monthName;
        }
    }

    private PartExtractor fullMonthExtractor(Locale locale) {
        return new FullMonthExtractor(locale, "MMMM");
    }

    static class ShortMonthExtractor extends FullMonthExtractor {
        public ShortMonthExtractor(Locale locale) {
            super(locale, "MMM");
        }

        @Override
        protected String[] getMonthsNamesFromLocale(Locale locale) {
            return new DateFormatSymbols(locale).getShortMonths();
        }
    }

    private PartExtractor shortMonthExtractor(Locale locale) {
        return new ShortMonthExtractor(locale);
    }

    static class RegularTimeExtractor extends RegexPartExtractor {

        RegularTimeExtractor() {
            super("(?:^|.*?\\D)"
                    + "("
                    + "(?<hours>(\\d|0\\d|1\\d|2[0-3]))"
                    + ":"
                    + "(?<minutes>[0-5]\\d)"
                    +    "(?:"
                    +    ":(?<seconds>[0-5]\\d)"
                    + "(?:\\.(?<milliseconds>\\d{1,9})?)?"
                    + ")?" // end of seconds
                    + "(?<ampm>\\s*[AaPp][Mm])?"
                    + ")" // end of main group
                    + "(?:$|\\D.*?)", null);
        }

        @Override
        public PartExtractorResult extract(String source) {
            Matcher m = pattern.matcher(source);
            if (m.matches()) {
                PartExtractorResult r = new PartExtractorResult();
                r.found = true;

                String ampmPart = "";
                String ampm = m.group("ampm");
                if (ampm != null) {
                    ampmPart = ampm.substring(0, ampm.length() - 2) + "a";
                }

                boolean hasAmPm = !ampmPart.isEmpty();

                String hourPart;

                if (hasAmPm) {
                    if (!m.group("hours").startsWith("0")) {
                        hourPart = "h"; // most often time with ampm is without leading zero
                    } else {
                        hourPart = "hh"; // unless it explicitly has leading zero
                    }
                } else {
                     hourPart = m.group("hours").length() == 1 ? "H" : "HH";
                }

                r.start = m.start("hours");
                if (m.group("milliseconds") != null) {
                    int millisecondsLength = m.group("milliseconds").length();
                    r.end = m.end("milliseconds");
                    r.formatterPattern = hourPart + ":mm:ss." + repeat("S", millisecondsLength);
                } else if (m.group("seconds") != null) {
                    r.end = m.end("seconds");
                    r.formatterPattern = hourPart + ":mm:ss";
                } else if (m.group("minutes") != null) {
                    r.end = m.end("minutes");
                    r.formatterPattern = hourPart + ":mm";
                } else {
                    r.end = m.end("hours");
                    r.formatterPattern = hourPart;
                }
                if (hasAmPm) {
                    r.formatterPattern += ampmPart;
                    r.end = m.end("ampm");
                }
                return r;
            }
            return new PartExtractorResult();
        }
    }

    static PartExtractor regularTimeExtractor = new RegularTimeExtractor();

    static class LookupResult {
        final List<Part> parts;
        final boolean found;
        LookupResult(List<Part> parts, boolean found) {
            this.parts = parts;
            this.found = found;
        }
    }
    private LookupResult lookup(List<Part> parts, PartExtractor partExtractor) {
        for (int i = 0; i < parts.size(); i++) {
            Part part = parts.get(i);

            if (part.state() == PartState.UNPARSED) {
                String source = part.source();
                PartExtractorResult per = partExtractor.extract(source);
                if (per.found) {
                    parts.remove(i);

                    if (per.end != source.length()) {
                        UnparsedPart after = new UnparsedPart(part.start() + per.end, part.end(), source.substring(per.end));
                        parts.add(i, after);
                    }

                    ParsedPart parsed = new ParsedPart(part.start() + per.start, part.start() + per.end, per.formatterPattern);
                    parts.add(i, parsed);

                    if (per.start != 0) {
                        UnparsedPart before = new UnparsedPart(part.start(), part.start() + per.start, source.substring(0, per.start));
                        parts.add(i, before);
                    }

                    return new LookupResult(parts, true);
                }
            }
        }
        return new LookupResult(parts, false);
    }

    private boolean haveUnparsed(List<Part> parts) {
        return parts.stream().anyMatch(p -> p.state() == PartState.UNPARSED);
    }

    private List<Part> markAsUnrecognized(List<Part> parts) {
        return parts.stream().map(p -> {
            if (p.state() == PartState.UNPARSED) {
                return new UnrecognizedPart(p);
            } else {
                return p;
            }
        }).collect(Collectors.toList());
    }

    private boolean notSet(Boolean val) {
        return val == null;
    }

    enum PartState {
        UNPARSED,
        PARSED,
        KNOWN_CONSTANT,
        UNRECOGNIZED
    }
    interface Part {
        int start(); // before symbol
        int end(); // after symbol
        PartState state();
        String source();
    }

    static class UnparsedPart implements Part {
        final int start;
        final int end;
        protected final String source;

        UnparsedPart(int start, int end, String source) {
            this.start = start;
            this.end = end;
            this.source = source;
        }
        @Override
        public int start() {
            return start;
        }
        @Override
        public int end() {
            return end;
        }
        @Override
        public PartState state() {
            return PartState.UNPARSED;
        }

        @Override
        public String source() {
            return source;
        }

        @Override
        public String toString() {
            return "UnparsedPart{" +
                    "start=" + start +
                    ", end=" + end +
                    ", source='" + source + '\'' +
                    '}';
        }
    }

    static class UnrecognizedPart extends UnparsedPart {

        public UnrecognizedPart(Part p) {
            super(p.start(), p.end(), p.source());
        }

        @Override
        public PartState state() {
            return PartState.UNRECOGNIZED;
        }

        @Override
        public String toString() {
            return "UnrecognizedPart{" +
                    "start=" + start +
                    ", end=" + end +
                    ", source='" + source + '\'' +
                    '}';
        }
    }
    static class ParsedPart implements Part {
        final int start;
        final int end;
        private final String pattern;

        ParsedPart(int start, int end, String pattern) {
            this.start = start;
            this.end = end;
            this.pattern = pattern;
        }

        @Override
        public int start() {
            return start;
        }

        @Override
        public int end() {
            return end;
        }

        @Override
        public PartState state() {
            return PartState.PARSED;
        }

        @Override
        public String source() {
            throw new IllegalStateException("Parsed part has no source");
        }

        public String getPattern() {
            return pattern;
        }
    }

    static String repeat(String key, int count) {
        return new String(new char[count]).replace("\0", key);
    }

//    public static void main(String[] args) {
//        String key = "z";
//        for (int i = 1; i < 10; i++) {
//            printPattern(key, i);
//        }
//    }

//    static void printPattern(String key, int count) {
//        String fullKey = new String(new char[count]).replace("\0", key);
////        ZonedDateTime now = ZonedDateTime.now();
//        ZonedDateTime now = ZonedDateTime.of(
//                LocalDate.of(2020, 1, 1),
//                LocalTime.of(1, 1, 1),
//                ZoneId.of("Europe/Kiev")
////                ZoneOffset.systemDefault()
////                ZoneOffset.UTC
//        );
//        try {
//            String formatted = now.format(DateTimeFormatter.ofPattern(fullKey));
//            System.out.println(fullKey + " -> " + formatted);
//        } catch (Exception e) {
//            System.out.println(fullKey + " -> " + "Error: " + e.getMessage());
//        }
//    }
    private class Chart {
        /**
         G - era designator
         GG -> AD / BC
         GGGG -> Anno Domini / Before Christ

         yy -> 24
         yyyy -> 2024

         Y - do not use (year of the week)

         MM -> 12
         MMM -> Dec/груд.
         MMMM -> December/грудня

         L - do not use (non-contextual month)
         LLLL -> December/грудень

         w - do not use (week of the year)
         W - do not use (Week in month)

         D - do not use (day of the year)

         d - day of the month
         d -> 5
         dd -> 05

         F - do not use (day of the week in month)

         EEE -> Thu
         EEEE -> Thursday

         u - day of the week (1 = Monday, ..., 7 = Sunday)
         u - do not use (day of the week)

         a - am/pm marker
         a -> PM

         H - hour in day (0-23)
         H -> 9
         HH -> 09

         k - do not use (hour in day (1-24))

         K - do not use (hour in am/pm (0-11))

         h	Hour in am/pm (1-12) ONLY IF am/pm marker is present
         h -> 1
         hh -> 01

         m - minute in hour
         m -> 1
         mm -> 01

         s - second in minute
         s -> 1
         ss -> 01

         S - millisecond (already defined)

         z - General time zone

         z -> UTC / EET BUT ZoneOffset.UTC -> Z
         zzz -> UTC / EET BUT ZoneOffset.UTC -> Z
         zzzz -> "Coordinated Universal Time" / "Eastern European Standard" Time BUT ZoneOffset.UTC -> Z

         Z - RFC 822 time zone
         Z -> +0200
         ZZ -> +0200
         ZZZ -> +0200
         ZZZZ -> GMT+02:00
         ZZZZZ -> +02:00

         X - ISO 8601 time zone
         X -> +02
         XX -> +0200
         XXX -> +02:00
         XXXX -> +0200
         XXXXX -> +02:00

         V	time-zone ID
         VV -> Europe/Kiev / Z for ZoneOffset.UTC and "Etc/UTC" for systemDefault

         v	generic time-zone name
         v -> EET
         vvvv -> Eastern European Time


         */
    }

}
