package liqp.filters.date.fuzzy.extractors;

import java.util.regex.Matcher;

class ISO8601YMDPatternExtractor extends RegexPartExtractor {

    public ISO8601YMDPatternExtractor() {
        super("(?:^|.*?\\D)"
                + "(?<year>\\d{4})-(?<month>0?[1-9]|1[0-2])-(?<date>0?[1-9]|[12][0-9]|3[01])"
                + "(?:$|\\D.*?)", null);
    }

    @Override
    public PartExtractorResult extract(String source) {
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            PartExtractorResult result = new PartExtractorResult();
            result.found = true;
            result.start = matcher.start("year");
            result.end = matcher.end("date");
            result.formatterPatterns = newList(getPattern(matcher));
            return result;
        }
        return new PartExtractorResult();
    }

    private String getPattern(Matcher matcher) {
        StringBuilder sbfp = new StringBuilder("yyyy");
        sbfp.append("-");
        if (matcher.group("month").length() == 1) {
            sbfp.append("M");
        } else {
            sbfp.append("MM");
        }
        sbfp.append("-");
        if (matcher.group("date").length() == 1) {
            sbfp.append("d");
        } else {
            sbfp.append("dd");
        }
        return sbfp.toString();
    }
}
