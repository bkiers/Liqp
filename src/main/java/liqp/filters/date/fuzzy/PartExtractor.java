package liqp.filters.date.fuzzy;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import liqp.filters.date.fuzzy.extractors.PartExtractorResult;

public interface PartExtractor {

    PartExtractorResult extract(String source, List<Part> parts, int i);

    default List<String> newList(String... el) {
        return Arrays.asList(el);
    }

    default List<String> appendToExisting(List<String> start, Supplier<List<String>> supplier) {
        if (start.isEmpty()) {
            return supplier.get();
        }
        return start.stream()
                .flatMap(prefix -> supplier.get().stream().map(suffix -> prefix + suffix))
                .collect(Collectors.toList());
    }
}
