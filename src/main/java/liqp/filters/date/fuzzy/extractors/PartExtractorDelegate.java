package liqp.filters.date.fuzzy.extractors;

import java.util.List;
import liqp.filters.date.fuzzy.Part;
import liqp.filters.date.fuzzy.PartExtractor;

public abstract class PartExtractorDelegate extends PartExtractor {

    protected PartExtractor delegate;

    public PartExtractorDelegate(String name) {
        super(name);
    }

    @Override
    public PartExtractorResult extract(String source, List<Part> parts, int i) {
        return delegate.extract(source, parts, i);
    }
}
