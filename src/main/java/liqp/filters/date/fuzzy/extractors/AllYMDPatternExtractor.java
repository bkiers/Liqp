package liqp.filters.date.fuzzy.extractors;

import static liqp.filters.date.fuzzy.extractors.AnyYMDPatternExtractor.pD;
import static liqp.filters.date.fuzzy.extractors.AnyYMDPatternExtractor.pM;
import static liqp.filters.date.fuzzy.extractors.AnyYMDPatternExtractor.pY2;
import static liqp.filters.date.fuzzy.extractors.AnyYMDPatternExtractor.pY4;
import static liqp.filters.date.fuzzy.extractors.AnyYMDPatternExtractor.pp;

import java.util.ArrayList;
import java.util.List;
import liqp.filters.date.fuzzy.PartExtractor;

public class AllYMDPatternExtractor implements PartExtractor {

    private final List<AnyYMDPatternExtractor> extractors = new ArrayList<>();

    public AllYMDPatternExtractor() {
        AnyYMDPatternExtractor iSO8601Y4MDPatternExtractor = new AnyYMDPatternExtractor(
                pY4(), pp("-"), pM(), pp("-"), pD()); // yyyy-MM-dd
        extractors.add(iSO8601Y4MDPatternExtractor);
        AnyYMDPatternExtractor americanY4MDPatternExtractor = new AnyYMDPatternExtractor(
                pM(), pp("/"), pD(), pp("/"), pY4()); // MM/dd/yyyy
        extractors.add(americanY4MDPatternExtractor);
        // next are top-rated locale formats, according to gpt
        AnyYMDPatternExtractor indianY4MDPatternExtractor = new AnyYMDPatternExtractor(
                pD(), pp("-"), pM(), pp("-"), pY4()); // d-M-yyyy
        extractors.add(indianY4MDPatternExtractor);
        AnyYMDPatternExtractor chineseY4MDPatternExtractor = new AnyYMDPatternExtractor(
                pY4(), pp("/"), pM(), pp("/"), pD()); // yyyy/M/d
        extractors.add(chineseY4MDPatternExtractor);
        AnyYMDPatternExtractor englishY4MDPatternExtractor = new AnyYMDPatternExtractor(
                pD(), pp("/"), pM(), pp("/"), pY4()); // d/M/yyyy
        extractors.add(englishY4MDPatternExtractor);
        AnyYMDPatternExtractor slavicY4MDPatternExtractor = new AnyYMDPatternExtractor(
                pD(), pp("."), pM(), pp("."), pY4());
        extractors.add(slavicY4MDPatternExtractor);
        AnyYMDPatternExtractor coldEuropeY4MDPatternExtractor = new AnyYMDPatternExtractor(
                pY4(), pp("-"), pM(), pp("-"), pD()); // yyyy-MM-dd
        extractors.add(coldEuropeY4MDPatternExtractor);
        AnyYMDPatternExtractor espanaY4MDPatternExtractor = new AnyYMDPatternExtractor(
                pY4(), pp("-"), pM(), pp("-"), pD()); // yyyy/MM/dd
        extractors.add(espanaY4MDPatternExtractor);
        AnyYMDPatternExtractor americanY2MDPatternExtractor = new AnyYMDPatternExtractor(
                pM(), pp("/"), pD(), pp("/"), pY4()); // MM/dd/yy
        extractors.add(americanY2MDPatternExtractor);
        AnyYMDPatternExtractor indianY2MDPatternExtractor = new AnyYMDPatternExtractor(
                pD(), pp("-"), pM(), pp("-"), pY2()); // d-M-yy
        extractors.add(indianY2MDPatternExtractor);
        AnyYMDPatternExtractor chineseY2MDPatternExtractor = new AnyYMDPatternExtractor(
                pY2(), pp("/"), pM(), pp("/"), pD()); // yyyy/M/d
        extractors.add(chineseY2MDPatternExtractor);
        AnyYMDPatternExtractor englishY2MDPatternExtractor = new AnyYMDPatternExtractor(
                pD(), pp("/"), pM(), pp("/"), pY2()); // d/M/yy
        extractors.add(englishY2MDPatternExtractor);
        AnyYMDPatternExtractor slavicY2MDPatternExtractor = new AnyYMDPatternExtractor(
                pD(), pp("."), pM(), pp("."), pY2());
        extractors.add(slavicY2MDPatternExtractor);
        AnyYMDPatternExtractor coldEuropeY2MDPatternExtractor = new AnyYMDPatternExtractor(
                pY2(), pp("-"), pM(), pp("-"), pD()); // yy-MM-dd
        extractors.add(coldEuropeY2MDPatternExtractor);
        AnyYMDPatternExtractor espanaY2MDPatternExtractor = new AnyYMDPatternExtractor(
                pY2(), pp("-"), pM(), pp("-"), pD()); // yy/MM/dd
        extractors.add(espanaY2MDPatternExtractor);
    }

    @Override
    public PartExtractorResult extract(String source) {
        for (AnyYMDPatternExtractor extractor : extractors) {
            PartExtractorResult result = extractor.extract(source);
            if (result.found) {
                return result;
            }
        }
        return new PartExtractorResult();
    }
}
