package liqp.filters.date.fuzzy.extractors;

import liqp.filters.date.fuzzy.PartExtractor;

public class PartExtractorDelegate implements PartExtractor {

    protected PartExtractor delegate;

    @Override
    public PartExtractorResult extract(String source) {
        return delegate.extract(source);
    }
}
