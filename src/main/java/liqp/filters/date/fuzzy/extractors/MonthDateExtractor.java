package liqp.filters.date.fuzzy.extractors;

import java.util.List;
import java.util.regex.Matcher;
import liqp.filters.date.fuzzy.LookupResult;
import liqp.filters.date.fuzzy.Part;
import liqp.filters.date.fuzzy.Part.RecognizedMonthNamePart;
import liqp.filters.date.fuzzy.PartExtractor;

public class MonthDateExtractor extends PartExtractor {

    public MonthDateExtractor() {
        super("MonthDateExtractor");
    }

    enum Mode {
        SPACES_ONLY,
        /**
         * not ",;" so far
         */
        NON_SEPARATORS
    }
    enum Direction {
        LEFT,
        RIGHT
    }

    // special classes name for easy debugging only (yu see class name directly)
    private static final RegexPartExtractor leftDateExtractor = new LeftDateExtractor(
            "(?:^|.*?\\D)(?<day>0?[1-9]|[12][0-9]|3[01])[^,\\d;]+?$");
    private static final RegexPartExtractor leftDateSpacesOnlyExtractor = new LeftDateSpacesOnlyExtractor(
            "(?:^|.*?\\D)(?<day>0?[1-9]|[12][0-9]|3[01])\\s+?$");
    private static final RegexPartExtractor rightDateExtractor = new RightDateExtractor(
            "^[^,\\d;]+?(?<day>0?[1-9]|[12][0-9]|3[01])(?:$|\\D.*?)");
    private static final RegexPartExtractor rightDateSpacesOnlyExtractor = new RightDateSpacesOnlyExtractor(
            "^\\s+?(?<day>0?[1-9]|[12][0-9]|3[01])(?:$|\\D.*?)");

    @Override
    public LookupResult extract(List<Part> parts) {
        // 1. find named month
        // if not - return empty result
        // then look both left and right for a day
        // comparing them (left vs right) by priority
        // so the situation like ' 11 december, 11 ' vs '11, december 11' will be resolved
        int monthIndex = getIndexByPartType(parts, RecognizedMonthNamePart.class);
        if (monthIndex == -1) {
            return new LookupResult(this.name, parts, false);
        }

        LookupResult rightResult = rightDate(monthIndex, parts, Mode.SPACES_ONLY);
        LookupResult leftResult = leftDate(monthIndex, parts, Mode.SPACES_ONLY);

        if (rightResult.found && leftResult.found) {
            throw new IllegalArgumentException("Month have date candidates on both sides");
        }
        if (leftResult.found) {
            return leftResult;
        }
        if (rightResult.found) {
            return rightResult;
        }


        rightResult = rightDate(monthIndex, parts, Mode.NON_SEPARATORS);
        leftResult = leftDate(monthIndex, parts, Mode.NON_SEPARATORS);
        if (rightResult.found && leftResult.found) {
            throw new IllegalArgumentException("Month have date candidates on both sides");
        }
        if (leftResult.found) {
            return leftResult;
        }
        if (rightResult.found) {
            return rightResult;
        }


        return new LookupResult(this.name, parts, false);
    }

    private LookupResult leftDate(int monthIndex, List<Part> parts, Mode mode) {
        RegexPartExtractor extractor = getExtractorByModeAndDirection(mode, Direction.LEFT);

        int index = monthIndex - 1;
        while (index >= 0) {
            LookupResult result = locatePart(parts, extractor, index);
            if (result != null) {
                return result;
            }
            index--;
        }

        return new LookupResult(this.name, parts, false);
    }

    private LookupResult rightDate(int monthIndex, List<Part> parts, Mode mode) {
        RegexPartExtractor extractor = getExtractorByModeAndDirection(mode, Direction.RIGHT);

        int index = monthIndex + 1;
        while (index < parts.size()) {
            LookupResult result = locatePart(parts, extractor, index);
            if (result != null) {
                return result;
            }
            index++;
        }

        if (extractor == null) {
            return new LookupResult(this.name, parts, false);
        } else {
            return new LookupResult(extractor.getName(), parts, false);
        }
    }

    private RegexPartExtractor getExtractorByModeAndDirection(Mode mode, Direction direction) {
        if (direction == Direction.LEFT) {
            if (mode == Mode.SPACES_ONLY) {
                return leftDateSpacesOnlyExtractor;
            } else if (mode == Mode.NON_SEPARATORS) {
                return leftDateExtractor;
            }
        } else {
            if (mode == Mode.SPACES_ONLY) {
                return rightDateSpacesOnlyExtractor;
            } else if (mode == Mode.NON_SEPARATORS) {
                return rightDateExtractor;
            }
        }
        return null;
    }

    private static class MonthDatePartExtractor extends RegexPartExtractor {

        public MonthDatePartExtractor(String name, String regex) {
            super(name, regex, null);
        }

        @Override
        public PartExtractorResult extract(String source, List<Part> parts, int i) {
            Matcher matcher = pattern.matcher(source);
            if (matcher.find()) {
                PartExtractorResult result = new PartExtractorResult(name);
                result.found = true;
                result.start = matcher.start("day");
                result.end = matcher.end("day");
                if (matcher.group("day").length() == 1) {
                    result.formatterPatterns = newList("d", "dd");
                } else {
                    result.formatterPatterns = newList("dd", "d");
                }
                return result;
            }
            return new PartExtractorResult(name);
        }
    }

    private static class LeftDateExtractor extends MonthDatePartExtractor {
        public LeftDateExtractor(String regex) {
            super("MonthDayExtractor.leftDateExtractor", regex);
        }
    }

    private static class LeftDateSpacesOnlyExtractor extends MonthDatePartExtractor {
        public LeftDateSpacesOnlyExtractor(String regex) {
            super("MonthDayExtractor.leftDateSpacesOnlyExtractor", regex);
        }
    }

    private static class RightDateExtractor extends MonthDatePartExtractor {
        public RightDateExtractor(String regex) {
            super("MonthDayExtractor.rightDateExtractor", regex);
        }
    }

    private static class RightDateSpacesOnlyExtractor extends MonthDatePartExtractor {
        public RightDateSpacesOnlyExtractor(String regex) {
            super("MonthDayExtractor.rightDateSpacesOnlyExtractor", regex);
        }
    }
}
