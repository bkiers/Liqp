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
            String resPattern = repeat("y", matcher.group("year").length());
            String era = matcher.group("era");
            if (!isBlank(era)) {
                String eraSeparator = matcher.group("eraSeparator");
                if (eraSeparator != null) {
                    resPattern += eraSeparator;
                }
                result.end = matcher.end("era");
                if (era.length() == 2) {
                    resPattern += "GG";
                } else {
                    resPattern += "GGGG";
                }
            } else {
                result.end = matcher.end("year");
            }
            result.formatterPatterns = newList(resPattern);
            return result;
        }
        return new PartExtractorResult();
    }
}