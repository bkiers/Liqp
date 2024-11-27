package liqp.filters.date;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FuzzyDateDateParser extends BasicDateParser {

    @Override
    public ZonedDateTime parse(String valAsString, Locale locale, ZoneId defaultZone) {
        String normalized = valAsString.toLowerCase();
        ZonedDateTime zonedDateTime = parseUsingCachedPatterns(normalized, locale, defaultZone);
        if (zonedDateTime != null) {
            return zonedDateTime;
        }

        List<Part> parts = new ArrayList<>();
        // we start as one big single unparsed part
        DateParseContext ctx = new DateParseContext();
        parts.add(new UnparsedPart(0, normalized.length(), normalized));

        while (haveUnparsed(parts)) {
            parts = parsePart(parts, ctx);
        }

        String pattern = reconstructPattern(parts);

        TemporalAccessor temporalAccessor = parseUsingPattern(normalized, pattern, locale);
        if (temporalAccessor == null) {
            return null;
        }
        storePattern(pattern);
        return getZonedDateTimeFromTemporalAccessor(temporalAccessor, defaultZone);
    }


    private String reconstructPattern(List<Part> parts) {
        return null;
    }

    static class DateParseContext {

    }

    private List<Part> parsePart(List<Part> parts, DateParseContext ctx) {
        return new ArrayList<>();
    }

    private boolean haveUnparsed(List<Part> parts) {
        return parts.stream().anyMatch(p -> p.state() == PartState.UNPARSED);
    }

    private PartItem getPart(String valAsString) {
        return null;
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
    }

    static class UnparsedPart implements Part {
        final int start;
        final int end;
        UnparsedPart(int start, int end, String value) {
            this.start = start;
            this.end = end;
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
    }

    static class PartItem {
        final PartKind kind;
        final String pattern;
        final int start;
        final int end;
        PartItem(PartKind kind, String pattern, int start, int end) {
            this.kind = kind;
            this.pattern = pattern;
            this.start = start;
            this.end = end;
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
