package liqp.filters.date.fuzzy.extractors;

import java.util.List;
import liqp.filters.date.fuzzy.Part;
import liqp.filters.date.fuzzy.PartExtractor;

public class PartExtractorDelegate implements PartExtractor {

    protected PartExtractor delegate;

    @Override
    public PartExtractorResult extract(String source, List<Part> parts, int i) {
        return delegate.extract(source, parts, i);
    }
}
