package liqp.filters.date.fuzzy;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import liqp.filters.date.fuzzy.Part.NewPart;
import liqp.filters.date.fuzzy.Part.RecognizedMonthNamePart;
import liqp.filters.date.fuzzy.Part.RecognizedPart;
import liqp.filters.date.fuzzy.extractors.PartExtractorResult;

public abstract class PartExtractor {

    public PartExtractorResult extract(String source, List<Part> parts, int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    protected List<String> newList(String... el) {
        return Arrays.asList(el);
    }

    protected List<String> appendToExisting(List<String> start, Supplier<List<String>> supplier) {
        if (start.isEmpty()) {
            return supplier.get();
        }
        return start.stream()
                .flatMap(prefix -> supplier.get().stream().map(suffix -> prefix + suffix))
                .collect(Collectors.toList());
    }

    public LookupResult extract(List<Part> parts) {
        for (int i = 0; i < parts.size(); i++) {
            Part part = parts.get(i);

            if (part.state() == Part.PartState.NEW) {
                String source = part.source();
                PartExtractorResult per = extract(source, parts, i);
                if (per.found) {
                    return getLookupResult(parts, i, per);
                }
            }
        }
        return new LookupResult("<none>", parts, false);
    }

    protected LookupResult getLookupResult(List<Part> parts, int i, PartExtractorResult per) {

        Part part = parts.get(i);
        String source = part.source();

        parts.remove(i);

        if (per.end != source.length()) {
            NewPart after = new NewPart(part.start() + per.end, part.end(), source.substring(per.end));
            parts.add(i, after);
        }

        RecognizedPart recognized;
        if (per.isMonthName) {
            recognized = new RecognizedMonthNamePart(part.start() + per.start, part.start() + per.end, per.formatterPatterns, source.substring(
                    per.start, per.end));
        } else {
            recognized = new RecognizedPart(part.start() + per.start, part.start() + per.end, per.formatterPatterns, source.substring(
                    per.start, per.end));
        }
        parts.add(i, recognized);

        if (per.start != 0) {
            NewPart before = new NewPart(
                    part.start(), part.start() + per.start, source.substring(0, per.start));
            parts.add(i, before);
        }

        return new LookupResult(per.extractorName, parts, true);
    }

}
