package liqp.filters.date.fuzzy.extractors;

import static liqp.LValue.isBlank;
import static liqp.filters.date.fuzzy.extractors.RegularTimeExtractor.repeat;

import java.util.List;
import java.util.regex.Matcher;
import liqp.filters.date.fuzzy.LookupResult;
import liqp.filters.date.fuzzy.Part;

class YearWithEra extends RegexPartExtractor {

    public YearWithEra() {
        super("YearWithEra", "(?:^|.*?\\D)(?<year>\\d+)(?<eraSeparator>\\s*)(?<era>AD|BC|Anno Domini|Before Christ)(?:$|\\D.*?)",
                null);
    }

    @Override
    public LookupResult extract(List<Part> parts) {
        return super.extract(parts);
    }

    @Override
    public PartExtractorResult extract(String source, List<Part> parts, int i) {
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            PartExtractorResult result = new PartExtractorResult("YearWithEra");
            result.found = true;
            result.start = matcher.start("year");
            String resPattern = "y";
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
        return new PartExtractorResult("YearWithEra");
    }
}
