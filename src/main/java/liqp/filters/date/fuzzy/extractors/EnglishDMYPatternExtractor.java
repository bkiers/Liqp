package liqp.filters.date.fuzzy.extractors;

class EnglishDMYPatternExtractor extends AnyYMDPatternExtractor {
    public EnglishDMYPatternExtractor() {
        super(pD(), pp("/"), pM(), pp("/"), pY(2, 4));
    }
}
