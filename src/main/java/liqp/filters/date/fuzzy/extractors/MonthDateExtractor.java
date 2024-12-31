package liqp.filters.date.fuzzy.extractors;

import java.util.List;
import java.util.regex.Matcher;
import liqp.filters.date.fuzzy.Part;
import liqp.filters.date.fuzzy.Part.RecognizedMonthNamePart;
import liqp.filters.date.fuzzy.Part.RecognizedPart;
import liqp.filters.date.fuzzy.PartExtractor;

public class MonthDateExtractor implements PartExtractor {

    @Override
    public PartExtractorResult extract(String source, List<Part> parts, int i) {
        // closest right or closest left should be a month
        if (rightIsMonth(parts, i)) {
            return leftDateExtractor.extract(source, parts, i);
        }
        if (leftIsMonth(parts, i)) {
            return rightDateExtractor.extract(source, parts, i);
        }
        return new PartExtractorResult("MonthDateExtractor");
    }

    private boolean leftIsMonth(List<Part> parts, int i) {
        int left = i - 1;
        while (left >= 0) {
            Part part = parts.get(left);
            if (part instanceof RecognizedMonthNamePart) {
                return true;
            }
            if (part instanceof RecognizedPart) {
                return false;
            }
            left--;
        }
        return false;
    }

    private boolean rightIsMonth(List<Part> parts, int i) {
        int right = i + 1;
        while (right < parts.size()) {
            Part part = parts.get(right);
            if (part instanceof RecognizedMonthNamePart) {
                return true;
            }
            if (part instanceof RecognizedPart) {
                return false;
            }
            right++;
        }
        return false;
    }


    private static final RegexPartExtractor leftDateExtractor = new MonthDatePartExtractor("MonthDayExtractor.left", "(?:^|.*?\\D)(?<day>0?[1-9]|[12][0-9]|3[01])\\D+?$");
    private static final RegexPartExtractor rightDateExtractor = new MonthDatePartExtractor("MonthDayExtractor.right", "^\\D+?(?<day>0?[1-9]|[12][0-9]|3[01])(?:$|\\D.*?)");
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
