package liqp.filters.date.fuzzy.extractors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import liqp.filters.date.fuzzy.PartExtractor;

class RegexPartExtractor implements PartExtractor {

    protected final Pattern pattern;
    protected final String formatterPattern;

    public RegexPartExtractor(String regex, String formatterPattern) {
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
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
