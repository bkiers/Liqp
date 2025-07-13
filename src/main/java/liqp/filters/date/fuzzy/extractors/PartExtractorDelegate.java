package liqp.filters.date.fuzzy.extractors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import liqp.filters.date.fuzzy.Part;
import liqp.filters.date.fuzzy.PartExtractor;

public class PartExtractorDelegate extends PartExtractor {

    protected List<PartExtractor> delegates = new ArrayList<>();

    public PartExtractorDelegate(String name) {
        super(name);
    }
    public PartExtractorDelegate(String name, PartExtractor... delegates) {
        super(name);
        this.delegates.addAll(Arrays.asList(delegates));
    }

    @Override
    public PartExtractorResult extract(String source, List<Part> parts, int i) {
        for (PartExtractor delegate : delegates) {
            PartExtractorResult result = delegate.extract(source, parts, i);
            if (result.found) {
                return result;
            }
        }
        return new PartExtractorResult(name);
    }
}
