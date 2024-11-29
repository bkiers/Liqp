package liqp.filters.date.fuzzy;

import static liqp.LValue.isBlank;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import liqp.filters.date.fuzzy.Part.NewPart;
import liqp.filters.date.fuzzy.Part.RecognizedMonthNamePart;
import liqp.filters.date.fuzzy.Part.RecognizedPart;
import liqp.filters.date.fuzzy.Part.RecognizedWeekDayPart;
import liqp.filters.date.fuzzy.Part.RecognizedYearWithoutEraPart;
import liqp.filters.date.fuzzy.extractors.PartExtractorResult;

public abstract class PartExtractor {

    /**
     * for debugging purposes
     */
    protected final String name;

    public PartExtractor(String name) {
        this.name = name;
    }

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
                visitPER(per);
                if (per.found) {
                    return getLookupResult(parts, i, per);
                }
            }
        }
        return new LookupResult("<none>", parts, false);
    }

    protected void visitPER(PartExtractorResult per) {

    }

    protected LookupResult getLookupResult(List<Part> parts, int i, PartExtractorResult per) {

        Part part = parts.get(i);
        String source = part.source();

        parts.remove(i);

        int recognizedEnd = part.start() + per.end;
        if (per.end != source.length()) {
            NewPart after = new NewPart(recognizedEnd, part.end(), source.substring(per.end));
            parts.add(i, after);
        }

        RecognizedPart recognized;
        int recognizedStart = part.start() + per.start;
        String recognizedSource = source.substring(per.start, per.end);
        if (per.isWeekDay) {
            recognized = new RecognizedWeekDayPart(recognizedStart, recognizedEnd, per.formatterPatterns, recognizedSource);
        } else if (per.yearWithoutEra) {
            recognized = new RecognizedYearWithoutEraPart(recognizedStart, recognizedEnd, per.formatterPatterns, recognizedSource);
        } else if (per.isMonthName) {
            recognized = new RecognizedMonthNamePart(recognizedStart, recognizedEnd, per.formatterPatterns, recognizedSource);
        } else {
            recognized = new RecognizedPart(recognizedStart, recognizedEnd, per.formatterPatterns, recognizedSource);
        }
        parts.add(i, recognized);

        if (per.start != 0) {
            NewPart before = new NewPart(
                    part.start(), recognizedStart, source.substring(0, per.start));
            parts.add(i, before);
        }

        return new LookupResult(per.extractorName, parts, true);
    }

    protected int getIndexByPartType(List<Part> parts, Class<? extends Part> partType) {
        for (int i = 0; i < parts.size(); i++) {
            Part part = parts.get(i);
            if (partType.isInstance(part)) {
                return i;
            }
        }
        return -1;
    }

    protected LookupResult locatePart(List<Part> parts, PartExtractor extractor, int index) {
        Part part = parts.get(index);
        if (part instanceof RecognizedPart) {
            return new LookupResult(this.name, parts, false);
        }
        if (part instanceof NewPart) {
            NewPart newPart = (NewPart) part;
            String source = newPart.source();
            if (!isBlank(source) && extractor != null) {
                PartExtractorResult result = extractor.extract(source, parts, index);
                if (result.found) {
                    return getLookupResult(parts, index, result);
                }
            }
        }
        return null;
    }
}
