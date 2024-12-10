package liqp.filters.date.fuzzy.extractors;

import static liqp.LValue.isBlank;
import static liqp.filters.date.fuzzy.extractors.RegularTimeExtractor.repeat;

import java.util.regex.Matcher;

class YearWithEra extends RegexPartExtractor {

    public YearWithEra() {
        super("(?:^|.*?\\D)(?<year>\\d+)(?<eraSeparator>\\s*)(?<era>AD|BC|Anno Domini|Before Christ)(?:$|\\D.*?)",
                null);
    }

    @Override
    public PartExtractorResult extract(String source) {
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            PartExtractorResult result = new PartExtractorResult();
            result.found = true;
            result.start = matcher.start("year");
            result.formatterPattern = repeat("y", matcher.group("year").length());
            String era = matcher.group("era");
            if (!isBlank(era)) {
                String eraSeparator = matcher.group("eraSeparator");
                if (eraSeparator != null) {
                    result.formatterPattern += eraSeparator;
                }
                result.end = matcher.end("era");
                if (era.length() == 2) {
                    result.formatterPattern += "GG";
                } else {
                    result.formatterPattern += "GGGG";
                }
            } else {
                result.end = matcher.end("year");
            }
            return result;
        }
        return new PartExtractorResult();
    }
}
