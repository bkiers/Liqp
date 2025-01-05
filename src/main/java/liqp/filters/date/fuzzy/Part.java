package liqp.filters.date.fuzzy;

import java.util.ArrayList;
import java.util.List;

public interface Part {
    enum PartState {
        NEW,
        RECOGNIZED,
        PUNCTUATION,
        UNRECOGNIZED
    }

    int start(); // before symbol
    int end(); // after symbol
    PartState state();
    String source();

    class NewPart implements Part {
        final int start;
        final int end;
        protected final String source;

        public NewPart(int start, int end, String source) {
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
            return PartState.NEW;
        }

        @Override
        public String source() {
            return source;
        }

        @Override
        public String toString() {
            return "NewPart{" +
                    "start=" + start +
                    ", end=" + end +
                    ", source='" + source + '\'' +
                    '}';
        }
    }

    class UnrecognizedPart extends NewPart {

        public UnrecognizedPart(Part p) {
            super(p.start(), p.end(), p.source());
        }

        public UnrecognizedPart(int start, int end, String source) {
            super(start, end, source.replace("'", "''"));
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

    class PunctuationPart extends NewPart {
        public static final String punctuationChars = "-:., /";
        public PunctuationPart(int start, int end, String source) {
            super(start, end, source);
        }

        @Override
        public PartState state() {
            return PartState.PUNCTUATION;
        }

        @Override
        public String toString() {
            return "PunctuationPart{" +
                    "start=" + start +
                    ", end=" + end +
                    ", source='" + source + '\'' +
                    '}';
        }
    }

    class RecognizedPart implements Part {
        final int start;
        final int end;
        protected final List<String> patterns;
        public final String source;

        public RecognizedPart(int start, int end, List<String> patterns, String source) {
            this.start = start;
            this.end = end;
            this.patterns = patterns;
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
            return PartState.RECOGNIZED;
        }

        @Override
        public String source() {
            throw new IllegalStateException("Parsed part has no source");
        }

        public List<String> getPatterns() {
            return patterns;
        }

        @Override
        public String toString() {
            return "RecognizedPart{" +
                    "start=" + start +
                    ", end=" + end +
                    ", pattern='" + patterns + '\'' +
                    '}';
        }
    }

    class RecognizedMonthNamePart extends RecognizedPart {
        public RecognizedMonthNamePart(int start, int end, List<String> patterns, String source) {
            super(start, end, patterns, source);
        }

        @Override
        public String toString() {
            return "RecognizedMonthNamePart{" +
                    "start=" + start +
                    ", end=" + end +
                    ", pattern='" + patterns + '\'' +
                    '}';
        }
    }
    class RecognizedYearWithoutEraPart extends RecognizedPart {
        public RecognizedYearWithoutEraPart(int start, int end, List<String> patterns, String source) {
            super(start, end, patterns, source);
        }

        @Override
        public String toString() {
            return "RecognizedYearWithoutEraPart{" +
                    "start=" + start +
                    ", end=" + end +
                    ", pattern='" + patterns + '\'' +
                    '}';
        }
    }

    class RecognizedWeekDayPart extends RecognizedPart {
        public RecognizedWeekDayPart(int start, int end, List<String> patterns, String source) {
            super(start, end, patterns, source);
        }

        @Override
        public String toString() {
            return "RecognizedWeekDayPart{" +
                    "start=" + start +
                    ", end=" + end +
                    ", pattern='" + patterns + '\'' +
                    '}';
        }
    }
}
