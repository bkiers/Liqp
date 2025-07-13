package liqp.filters.date.fuzzy.extractors;

import static liqp.LValue.isBlank;

import java.util.List;
import java.util.regex.Matcher;
import liqp.filters.date.fuzzy.LookupResult;
import liqp.filters.date.fuzzy.Part;
import liqp.filters.date.fuzzy.PartExtractor;

public class EraAfterYearExtractor extends PartExtractor {

    private final PartExtractor eraExtractor = new RegexPartExtractor("Era",
            "(?:^|.*?\\s)(?<era>AD|BC|Anno Domini|Before Christ)(?:$|\\s.*?)", null) {
        @Override
        public PartExtractorResult extract(String source, List<Part> parts, int i) {
            Matcher matcher = pattern.matcher(source);
            if (matcher.find()) {
                PartExtractorResult result = new PartExtractorResult("Era");
                result.found = true;
                result.start = matcher.start("era");
                result.end = matcher.end("era");
                String era = matcher.group("era");
                if (era.length() == 2) {
                    result.formatterPatterns = newList("GG");
                } else {
                    result.formatterPatterns = newList("GGGG");
                }
                return result;
            }
            return new PartExtractorResult("Era");
        }
    };

    public EraAfterYearExtractor() {
        super("EraAfterYear");
    }

    @Override
    public LookupResult extract(List<Part> parts) {
        int yearPartIndex = getIndexByPartType(parts, Part.RecognizedYearWithoutEraPart.class);
        if (yearPartIndex == -1) {
            return new LookupResult(name, parts, false);
        }
        int i = yearPartIndex + 1;
        while (i < parts.size()) {
            LookupResult result = locatePart(parts, eraExtractor, i);
            if (result != null) {
                return result;
            }
            i++;
        }

        return new LookupResult(name, parts, false);
    }

}
