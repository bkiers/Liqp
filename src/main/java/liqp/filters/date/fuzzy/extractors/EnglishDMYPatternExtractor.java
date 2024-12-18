package liqp.filters.date.fuzzy.extractors;

import java.util.regex.Matcher;

class EnglishDMYPatternExtractor extends RegexPartExtractor {
    public EnglishDMYPatternExtractor() {
        super("(?:^|.*?\\D)"
                + "(?<day>0?[1-9]|[12][0-9]|3[01])"
                + "/"
                + "(?<month>0?[1-9]|1[0-2])"
                + "/"
                + "(?<year>\\d{2}|\\d{4})"
                + "(?:$|\\D.*?)", null);
    }


    @Override
    public PartExtractorResult extract(String source) {
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            PartExtractorResult result = new PartExtractorResult();
            result.found = true;
            result.start = matcher.start("day");
            result.end = matcher.end("year");
            result.formatterPattern = getPattern(matcher);
            return result;
        }
        return new PartExtractorResult();
    }

    private String getPattern(Matcher matcher) {
        StringBuilder sbfp = new StringBuilder();
        if (matcher.group("day").length() == 1) {
            sbfp.append("d");
        } else {
            sbfp.append("dd");
        }
        sbfp.append("/");
        if (matcher.group("month").length() == 1) {
            sbfp.append("M");
        } else {
            sbfp.append("MM");
        }
        sbfp.append("/");
        if (matcher.group("year").length() == 2) {
            sbfp.append("yy");
        } else {
            sbfp.append("yyyy");
        }
        return sbfp.toString();
    }
}
