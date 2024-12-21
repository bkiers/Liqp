package liqp.filters.date.fuzzy;

import static liqp.filters.date.fuzzy.extractors.Extractors.allYMDPatternExtractor;
import static liqp.filters.date.fuzzy.extractors.Extractors.fullMonthExtractor;
import static liqp.filters.date.fuzzy.extractors.Extractors.fullWeekdaysExtractor;
import static liqp.filters.date.fuzzy.extractors.Extractors.plainYearExtractor;
import static liqp.filters.date.fuzzy.extractors.Extractors.regularTimeExtractor;
import static liqp.filters.date.fuzzy.extractors.Extractors.shortMonthExtractor;
import static liqp.filters.date.fuzzy.extractors.Extractors.shortWeekdaysExtractor;
import static liqp.filters.date.fuzzy.extractors.Extractors.yearWithEraExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import liqp.filters.date.fuzzy.Part.NewPart;
import liqp.filters.date.fuzzy.Part.PunctuationPart;
import liqp.filters.date.fuzzy.Part.RecognizedPart;
import liqp.filters.date.fuzzy.Part.UnrecognizedPart;
import liqp.filters.date.fuzzy.extractors.PartExtractorResult;

public class PartRecognizer {

    private static final String punctuationChars = "-:., /";

    List<Part> recognizePart(List<Part> parts, DatePatternRecognizingContext ctx) {

        if (notSet(ctx.weekDay)) {
            LookupResult result = lookup(parts, fullWeekdaysExtractor.get(ctx.locale));
            if (result.found) {
                ctx.weekDay = true;
                return result.parts;
            }
            result = lookup(parts, shortWeekdaysExtractor.get(ctx.locale));
            if (result.found) {
                ctx.weekDay = true;
                return result.parts;
            }
            ctx.weekDay = false;
        }

        if (notSet(ctx.hasYear)) {
            LookupResult result = lookup(parts, yearWithEraExtractor.get(ctx.locale));
            if (result.found) {
                ctx.hasYear = true;
                return result.parts;
            }
        }
        if (notSet(ctx.hasTime)) {
            LookupResult result = lookup(parts, regularTimeExtractor.get(ctx.locale));
            if (result.found) {
                ctx.hasTime = true;
                return result.parts;
            }
            ctx.hasTime = false;
        }
        if (notSet(ctx.hasYear)) {
            LookupResult result = lookup(parts, allYMDPatternExtractor.get(ctx.locale));
            if (result.found) {
                ctx.hasYear = true;
                ctx.hasMonth = true;
                ctx.hasDay = true;
                return result.parts;
            }
        }

        if (notSet(ctx.hasYear)) {
            LookupResult result = lookup(parts, plainYearExtractor.get(ctx.locale));
            if (result.found) {
                ctx.hasYear = true;
                return result.parts;
            }
            // last "year check" and since we are here - there is no year
            ctx.hasYear = false;
        }
        if (notSet(ctx.hasMonth)) {
            LookupResult result = lookup(parts, fullMonthExtractor.get(ctx.locale));
            if (result.found) {
                ctx.hasMonth = true;
                return result.parts;
            }

            result = lookup(parts, shortMonthExtractor.get(ctx.locale));
            if (result.found) {
                ctx.hasMonth = true;
                return result.parts;
            }

            ctx.hasMonth = false;
        }

        if (notSet(ctx.hasDay)) {
            LookupResult result = lookup(parts, fullMonthExtractor.get(ctx.locale));
            if (result.found) {
                ctx.hasDay = true;
                return result.parts;
            }

            result = lookup(parts, shortMonthExtractor.get(ctx.locale));
            if (result.found) {
                ctx.hasDay = true;
                return result.parts;
            }

            ctx.hasDay = false;
        }

        return markAsUnrecognized(parts);
    }

    private boolean notSet(Boolean val) {
        return val == null;
    }
    private LookupResult lookup(List<Part> parts, PartExtractor partExtractor) {
        for (int i = 0; i < parts.size(); i++) {
            Part part = parts.get(i);

            if (part.state() == Part.PartState.NEW) {
                String source = part.source();
                PartExtractorResult per = partExtractor.extract(source);
                if (per.found) {
                    parts.remove(i);

                    if (per.end != source.length()) {
                        NewPart after = new NewPart(part.start() + per.end, part.end(), source.substring(per.end));
                        parts.add(i, after);
                    }

                    RecognizedPart recognized = new RecognizedPart(part.start() + per.start, part.start() + per.end, per.formatterPatterns);
                    parts.add(i, recognized);

                    if (per.start != 0) {
                        NewPart before = new NewPart(part.start(), part.start() + per.start, source.substring(0, per.start));
                        parts.add(i, before);
                    }

                    return new LookupResult(parts, true);
                }
            }
        }
        return new LookupResult(parts, false);
    }

    private List<Part> markAsUnrecognized(List<Part> parts) {
        List<Part> newParts = new ArrayList<>();
        for (Part p : parts) {
            if (p.state() == Part.PartState.NEW) {
                newParts.addAll(categorizeUnrecognized(p));
            } else {
                newParts.add(p);
            }
        }
        return newParts;
    }

    private List<Part> categorizeUnrecognized(Part p) {
        String input = p.source();
        if (input.isEmpty()) {
            return Collections.singletonList(new UnrecognizedPart(p));
        }
        List<Part> parts = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        boolean isPunctuation = isPunctuation(input.charAt(0));
        int partStart = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (isPunctuation(c) == isPunctuation) {
                buffer.append(c);
            } else {
                // Add the previous part
                parts.add(createPart(partStart, i, buffer.toString(), isPunctuation));
                buffer.setLength(0); // Clear the buffer
                buffer.append(c); // Start a new part
                partStart = i;
                isPunctuation = !isPunctuation;
            }
        }
        if (buffer.length() > 0) {
            parts.add(createPart(partStart, input.length(), buffer.toString(), isPunctuation));
        }

        return parts;
    }

    private static Part createPart(int start, int end, String content, boolean isPunctuation) {
        return isPunctuation ?
                  new PunctuationPart(start, end, content)
                : new UnrecognizedPart(start, end, content);
    }

    private static boolean isPunctuation(char c) {
        return punctuationChars.indexOf(c) >= 0;
    }
}
