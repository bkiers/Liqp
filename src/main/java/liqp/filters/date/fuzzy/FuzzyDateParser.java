package liqp.filters.date.fuzzy;

import java.text.DateFormatSymbols;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import liqp.filters.date.BasicDateParser;
import liqp.filters.date.fuzzy.Part.NewPart;
import liqp.filters.date.fuzzy.Part.RecognizedPart;

/**
 * This package is writen for fun.
 * Even if it may be extended to be very powerful, this is not intention. For example, it may
 * use locale information to guess some pattern parts,
 * like "leading zeros appear to be less commonly used in Germany than in Austria and Switzerland".
 * But it is not implemented. And not going to be.
 * It simply must cover all possible cases of format that been used in previous
 * implementation of BasicDateParser. Which in fact is quite poor yet did the work well.
 * So not expect super-magic here.
 * And yes.
 * It is called "fuzzy" but is having strict and well-defined rules of "guessing".
 * So this class is quite exact. See test for details.
 *
 * And if you think I am too enthusiastic about this package -
 * you just haven't seen native Ruby implementation.
 * See their tests: <a href="https://github.com/ruby/date/blob/master/test/date/test_date_parse.rb">ruby date parse tests</a>
 * and the code: <a href="https://github.com/ruby/date/blob/master/ext/date/date_parse.c">ruby date parse</a>
 *
 *
 * Some refs:
 * https://github.com/pixa-pics/pixa-pics.github.io/blob/main/src/js/utils/time.js
 * https://github.com/ruby-rdf/rdf-tabular/blob/develop/spec/uax35_spec.rb
 * https://ielts.idp.com/prepare/article-how-to-write-the-date-correctly
 * https://en.wikipedia.org/wiki/Date_format_by_country
 * https://en.wikipedia.org/wiki/Date_and_time_notation_in_Germany
 * https://en.wikipedia.org/wiki/Date_and_time_representation_by_country
 */
public class FuzzyDateParser extends BasicDateParser {

    public static boolean CLDR_LOADED = new DateFormatSymbols(Locale.GERMANY)
            .getShortMonths()[1]
            .endsWith(".");
    private static final PartRecognizer partRecognizer = new PartRecognizer();

    @Override
    public ZonedDateTime parse(String normalized, Locale locale, ZoneId defaultZone) {
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        if (defaultZone == null) {
            defaultZone = ZoneId.systemDefault();
        }
        ZonedDateTime date = parseUsingCachedPatterns(normalized, locale, defaultZone);
        if (date != null) {
            return date;
        }

        GuessingResult guessingResult = guessPattern(normalized, locale, defaultZone);
        if (guessingResult == null) {
            return null;
        }
        storePattern(guessingResult.pattern);
        return getFullDateIfPossible(guessingResult.temporalAccessor, defaultZone);
    }

    GuessingResult guessPattern(String normalized, Locale locale, ZoneId defaultZone) {
        Stream<String> guessingStream = getGuessingStream(cachedPatterns, normalized, locale, defaultZone);
        return getGuessingResult(guessingStream, normalized, locale, defaultZone);
    }

    private Stream<String> getGuessingStream(List<String> cachedPatterns, String normalized,
            Locale locale, ZoneId defaultZone) {
        // [1, 2][1][1][1] => ["1111"], ["2111"]
        List<List<String>> fullPattern = guessVariants(normalized, locale);

        return fullPattern.stream()
                .reduce(
                        Stream.of(""), // Initial
                        (stream, list) -> stream.flatMap(
                                combination -> list.stream().map(element -> combination + element)
                        ),
                        Stream::concat
                )
                .filter(p -> !cachedPatterns.contains(p));
    }


    protected List<List<String>> guessVariants(String normalized, Locale locale) {
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        List<Part> parts = new ArrayList<>();
        DatePatternRecognizingContext ctx = new DatePatternRecognizingContext(locale);
        parts.add(new NewPart(0, normalized.length(), normalized));

        while (haveUnrecognized(parts)) {
            parts = partRecognizer.recognizePart(parts, ctx);
        }

        return reconstructPattern(parts);
    }

    private List<List<String>> reconstructPattern(List<Part> parts) {
        return parts.stream().map(p -> {
            if (p.state() == Part.PartState.RECOGNIZED) {
                return ((RecognizedPart) p).getPatterns();
            } else if (p.state() == Part.PartState.PUNCTUATION) {
                return newList(p.source());
            } else {
                return newList("'" + p.source() + "'");
            }
        }).collect(Collectors.toList());
    }

    private List<String> newList(String pattern) {
        List<String> res = new ArrayList<>();
        res.add(pattern);
        return res;
    }

    private GuessingResult getGuessingResult(Stream<String> guessingStream, String normalized, Locale locale, ZoneId defaultZone) {
        return guessingStream
                .map(pattern -> {
                    TemporalAccessor temporalAccessor = parseUsingPattern(normalized, pattern, locale);
                    if (temporalAccessor != null) {
                        GuessingResult result = new GuessingResult();
                        result.pattern = pattern;
                        result.temporalAccessor = temporalAccessor;
                        return result;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    static class GuessingResult {
        String pattern;
        TemporalAccessor temporalAccessor;
    }
    private TemporalAccessor getTemporalAccessor(String normalized, Locale locale,
            ZoneId defaultZone, Stream<String> guessingStream) {
        return null;
    }

    private boolean haveUnrecognized(List<Part> parts) {
        return parts.stream().anyMatch(p -> p.state() == Part.PartState.NEW);
    }
}