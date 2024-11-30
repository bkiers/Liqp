package liqp.filters.date;

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

public class FuzzyDateParser extends BasicDateParser {

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
        boolean found;
        int start;
        int end;
    }
    interface PartExtractor {
        PartExtractorResult extract(String source);
        String formatterPattern();
    }
    static class RegexPartExtractor implements PartExtractor {
        private final Pattern pattern;
        private final String formatterPattern;

        RegexPartExtractor(String regex, String formatterPattern) {
            this.pattern = Pattern.compile(regex);
            this.formatterPattern = formatterPattern;
        }

        @Override
        public PartExtractorResult extract(String source) {
            Matcher matcher = pattern.matcher(source);
            if (matcher.find()) {
                PartExtractorResult result = new PartExtractorResult();
                result.found = true;
                result.start = matcher.start(1);
                result.end = matcher.end(1);
                return result;
            }
            return new PartExtractorResult();
        }

        @Override
        public String formatterPattern() {
            return formatterPattern;
        }
    }
    PartExtractor plainYearExtractor = new RegexPartExtractor(".*\\b?(\\d{4})\\b?.*", "yyyy");

    static class PartExtractorDelegate implements PartExtractor {
        private PartExtractor delegate;

        @Override
        public PartExtractorResult extract(String source) {
            return delegate.extract(source);
        }

        @Override
        public String formatterPattern() {
            return delegate.formatterPattern();
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

        private static String[] withoutNulls(String[] shortMonths) {
            return Arrays.stream(shortMonths)
                    .filter(month -> month != null && !month.isEmpty())
                    .toArray(String[]::new);
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


    static class LookupResult {
        final List<Part> parts;
        final boolean found;
        LookupResult(List<Part> parts, boolean found) {
            this.parts = parts;
            this.found = found;
        }
    }
    private List<Part> parsePart(List<Part> parts, DateParseContext ctx) {
        if (notSet(ctx.hasYear)) {
            LookupResult result = lookup(parts, plainYearExtractor);
            if (result.found) {
                ctx.hasYear = true;
                return result.parts;
            }
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

        if (notSet(ctx.hasTime)) {
            LookupResult result = new LookupResult(parts, false);
            if (result.found) {
                ctx.hasTime = true;
                return result.parts;
            }
            ctx.hasTime = false;
        }
        return markAsUnrecognized(parts);
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

                    ParsedPart parsed = new ParsedPart(part.start() + per.start, part.start() + per.end, partExtractor.formatterPattern());
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
        private final String source;

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

    enum PartKind {
        CONSTANT,
        YEAR,
        MONTH,
        DAY,
        HOUR,
        MINUTE,
        SECOND,
        MILLISECOND,
        MICROSECOND,
        NANOSECOND
    }

}
