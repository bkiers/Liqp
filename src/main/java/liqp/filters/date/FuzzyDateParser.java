package liqp.filters.date;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
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

        String pattern = guessPattern(normalized);

        TemporalAccessor temporalAccessor = parseUsingPattern(normalized, pattern, locale);
        if (temporalAccessor == null) {
            return null;
        }
        storePattern(pattern);
        return getZonedDateTimeFromTemporalAccessor(temporalAccessor, defaultZone);
    }

    String guessPattern(String normalized) {
        List<Part> parts = new ArrayList<>();
        // we start as one big single unparsed part
        DateParseContext ctx = new DateParseContext();
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
        Boolean hasYear;
    }

    static class PatternPair {
        final Pattern pattern;
        final String formatterPattern;

        PatternPair(Pattern pattern, String formatterPattern) {
            this.pattern = pattern;
            this.formatterPattern = formatterPattern;
        }
    }
    static final PatternPair plainYearPair = new PatternPair(Pattern.compile(".*\\b?(\\d{4})\\b?.*"), "yyyy");
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
            LookupResult result = lookup(parts, plainYearPair);
            if (result.found) {
                ctx.hasYear = true;
                return result.parts;
            } else {
                ctx.hasYear = false;
            }
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


    private LookupResult lookup(List<Part> parts, PatternPair patternPair) {
        for (int i = 0; i < parts.size(); i++) {
            Part part = parts.get(i);

            if (part.state() == PartState.UNPARSED) {
                String source = part.source();
                Matcher matcher = patternPair.pattern.matcher(source);
                if (matcher.find()) {
                    parts.remove(i);

                    if (matcher.end(1) != source.length()) {
                        UnparsedPart after = new UnparsedPart(part.start() + matcher.end(1), part.end(), source.substring(matcher.end(1)));
                        parts.add(i, after);
                    }

                    ParsedPart parsed = new ParsedPart(part.start() + matcher.start(1), part.start() + matcher.end(1), patternPair.formatterPattern);
                    parts.add(i, parsed);

                    if (matcher.start(1) != 0) {
                        UnparsedPart before = new UnparsedPart(part.start(), part.start() + matcher.start(1), source.substring(0, matcher.start(1)));
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
