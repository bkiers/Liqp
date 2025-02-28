package liqp.filters.date.fuzzy.extractors;

import static liqp.filters.date.fuzzy.extractors.AnyYMDPatternExtractor.pD;
import static liqp.filters.date.fuzzy.extractors.AnyYMDPatternExtractor.pM;
import static liqp.filters.date.fuzzy.extractors.AnyYMDPatternExtractor.pMn;
import static liqp.filters.date.fuzzy.extractors.AnyYMDPatternExtractor.pY2;
import static liqp.filters.date.fuzzy.extractors.AnyYMDPatternExtractor.pY4;
import static liqp.filters.date.fuzzy.extractors.AnyYMDPatternExtractor.pp1;
import static liqp.filters.date.fuzzy.extractors.AnyYMDPatternExtractor.pp2;

import java.util.ArrayList;
import java.util.List;
import liqp.filters.date.fuzzy.Part;
import liqp.filters.date.fuzzy.PartExtractor;

public class AllYMDPatternExtractor extends PartExtractor {

    private final List<AnyYMDPatternExtractor> extractors = new ArrayList<>();

    public AllYMDPatternExtractor() {
        super("AllYMDPatternExtractor");
        extractors.add(new AnyYMDPatternExtractor("iSO8601Y4MDPatternExtractor",
                pY4(), pp1("-"), pM(), pp2("-"), pD())); // yyyy-MM-dd

        extractors.add(new AnyYMDPatternExtractor("americanY4MDPatternExtractor",
                pM(), pp1("/"), pD(), pp2("/"), pY4())); // MM/dd/yyyy
        // next are top-rated locale formats, according to gpt
        extractors.add(new AnyYMDPatternExtractor("indianY4MDPatternExtractor",
                pD(), pp1("-"), pM(), pp2("-"), pY4())); // d-M-yyyy
        extractors.add(new AnyYMDPatternExtractor("chineseY4MDPatternExtractor",
                pY4(), pp1("/"), pM(), pp2("/"), pD())); // yyyy/M/d
        extractors.add(new AnyYMDPatternExtractor("englishY4MDPatternExtractor",
                pD(), pp1("/"), pM(), pp2("/"), pY4())); // d/M/yyyy
        extractors.add(new AnyYMDPatternExtractor("slavicY4MDPatternExtractor",
                pD(), pp1("."), pM(), pp2("."), pY4())); // dd.MM.yyyy
        extractors.add(new AnyYMDPatternExtractor("coldEuropeY4MDPatternExtractor",
                pY4(), pp1("-"), pM(), pp2("-"), pD())); // yyyy-MM-dd
        extractors.add(new AnyYMDPatternExtractor("espanaY4MDPatternExtractor",
                pY4(), pp1("-"), pM(), pp2("-"), pD())); // yyyy/MM/dd

        extractors.add(new AnyYMDPatternExtractor("americanY2MDPatternExtractor",
                pM(), pp1("/"), pD(), pp2("/"), pY2())); // MM/dd/yy
        extractors.add(new AnyYMDPatternExtractor("indianY2MDPatternExtractor",
                pD(), pp1("-"), pM(), pp2("-"), pY2())); // d-M-yy
        extractors.add(new AnyYMDPatternExtractor("chineseY2MDPatternExtractor",
                pY2(), pp1("/"), pM(), pp2("/"), pD())); // yy/M/d
        extractors.add(new AnyYMDPatternExtractor("englishY2MDPatternExtractor",
                pD(), pp1("/"), pM(), pp2("/"), pY2())); // d/M/yy
        extractors.add(new AnyYMDPatternExtractor("slavicY2MDPatternExtractor",
                pD(), pp1("."), pM(), pp2("."), pY2())); // dd.MM.yy
        extractors.add(new AnyYMDPatternExtractor("coldEuropeY2MDPatternExtractor",
                pY2(), pp1("-"), pM(), pp2("-"), pD())); // yy-MM-dd
        extractors.add(new AnyYMDPatternExtractor("espanaY2MDPatternExtractor",
                pY2(), pp1("-"), pM(), pp2("-"), pD())); // yy/MM/dd
        extractors.add(new AnyYMDPatternExtractor("RFC822Y4MDPatternExtractor",
                pD(), pp1(" "), pMn(), pp2(" "), pY4())); // dd MMMM yyyy
        extractors.add(new AnyYMDPatternExtractor("RFC822Y2MDPatternExtractor",
                pD(), pp1(" "), pMn(), pp2(" "), pY2())); // dd MMMM yy
    }

    @Override
    public PartExtractorResult extract(String source, List<Part> parts, int i) {
        for (AnyYMDPatternExtractor extractor : extractors) {
            PartExtractorResult result = extractor.extract(source, parts, i);
            if (result.found) {
                return result;
            }
        }
        return new PartExtractorResult(this.name);
    }
}
