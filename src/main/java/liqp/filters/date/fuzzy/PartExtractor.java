package liqp.filters.date.fuzzy;

import liqp.filters.date.fuzzy.extractors.PartExtractorResult;

public interface PartExtractor {

    PartExtractorResult extract(String source);
}
