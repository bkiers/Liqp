package liqp.filters.date.fuzzy.extractors;

import static liqp.LValue.isBlank;

import java.util.List;
import java.util.regex.Matcher;
import liqp.filters.date.fuzzy.LookupResult;
import liqp.filters.date.fuzzy.Part;
import liqp.filters.date.fuzzy.Part.NewPart;
import liqp.filters.date.fuzzy.Part.RecognizedMonthNamePart;
import liqp.filters.date.fuzzy.Part.RecognizedPart;
import liqp.filters.date.fuzzy.PartExtractor;

public class MonthDateExtractor extends PartExtractor {

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
    @Override
    public LookupResult extract(List<Part> parts) {
        // 1. find named month
        // if not - return empty result
        // then look both left and right for a day
        // comparing them (left vs right) by priority
        // so the situation like ' 11 december, 11 ' vs '11, december 11' will be resolved
        int monthIndex = lookForNamedMonth(parts);
        if (monthIndex == -1) {
            return new LookupResult("MonthDateExtractor", parts, false);
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
        if (rightResult.found) {
            return rightResult;
        }
        if (leftResult.found) {
            return leftResult;
        }


        return new LookupResult("MonthDateExtractor", parts, false);
    }

    private LookupResult leftDate(int monthIndex, List<Part> parts, Mode mode) {
        RegexPartExtractor extractor = getExtractorByModeAndDirection(mode, Direction.LEFT);

        int index = monthIndex - 1;
        while (index >= 0) {
            LookupResult result = locateDate(parts, extractor, index);
            if (result != null) {
                return result;
            }
            index--;
        }

        return new LookupResult("MonthDateExtractor", parts, false);
    }

    private LookupResult rightDate(int monthIndex, List<Part> parts, Mode mode) {
        RegexPartExtractor extractor = getExtractorByModeAndDirection(mode, Direction.RIGHT);

        int index = monthIndex + 1;
        while (index < parts.size()) {
            LookupResult result = locateDate(parts, extractor, index);
            if (result != null) {
                return result;
            }
            index++;
        }

        return new LookupResult("MonthDateExtractor", parts, false);
    }

    private LookupResult locateDate(List<Part> parts, RegexPartExtractor extractor, int index) {
        Part part = parts.get(index);
        if (part instanceof RecognizedPart) {
            return new LookupResult("MonthDateExtractor", parts, false);
        }
        if (part instanceof NewPart) {
            NewPart newPart = (NewPart) part;
            String source = newPart.source();
            if (!isBlank(source) && extractor != null) {
                PartExtractorResult leftResult = extractor.extract(source, parts, index);
                if (leftResult.found) {
                    return getLookupResult(parts, index, leftResult);
                }
            }
        }
        return null;
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

    private int lookForNamedMonth(List<Part> parts) {
        for (int i = 0; i < parts.size(); i++) {
            Part part = parts.get(i);
            if (part instanceof RecognizedMonthNamePart) {
                return i;
            }
        }
        return -1;
    }

    private static final RegexPartExtractor leftDateExtractor = new MonthDatePartExtractor("MonthDayExtractor.left",
            "(?:^|.*?\\D)(?<day>0?[1-9]|[12][0-9]|3[01])[^,\\d;]+?$");
    private static final RegexPartExtractor leftDateSpacesOnlyExtractor = new MonthDatePartExtractor("MonthDayExtractor.left",
            "(?:^|.*?\\D)(?<day>0?[1-9]|[12][0-9]|3[01])\\s+?$");
    private static final RegexPartExtractor rightDateExtractor = new MonthDatePartExtractor("MonthDayExtractor.right",
            "^[^,\\d;]+?(?<day>0?[1-9]|[12][0-9]|3[01])(?:$|\\D.*?)");
    private static final RegexPartExtractor rightDateSpacesOnlyExtractor = new MonthDatePartExtractor("MonthDayExtractor.right",
            "^\\s+?(?<day>0?[1-9]|[12][0-9]|3[01])(?:$|\\D.*?)");
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
}
