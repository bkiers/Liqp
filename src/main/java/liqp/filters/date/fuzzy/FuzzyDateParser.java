package liqp.filters.date.fuzzy;

import java.text.DateFormatSymbols;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
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
        ZonedDateTime zonedDateTime = parseUsingCachedPatterns(normalized, locale, defaultZone);
        if (zonedDateTime != null) {
            return zonedDateTime;
        }

        String pattern = guessPattern(normalized, locale);

        TemporalAccessor temporalAccessor = parseUsingPattern(normalized, pattern, locale);
        if (temporalAccessor == null) {
            return null;
        }
        storePattern(pattern);
        return getZonedDateTimeFromTemporalAccessor(temporalAccessor, defaultZone);
    }

    String guessPattern(String normalized, Locale locale) {
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

    private boolean haveUnrecognized(List<Part> parts) {
        return parts.stream().anyMatch(p -> p.state() == Part.PartState.NEW);
    }

    private String reconstructPattern(List<Part> parts) {
        return parts.stream().map(p -> {
            if (p.state() == Part.PartState.RECOGNIZED) {
                return ((RecognizedPart) p).getPattern();
            } else if (p.state() == Part.PartState.PUNCTUATION) {
                return p.source();
            } else {
                return "'" + p.source() + "'";
            }
        }).collect(Collectors.joining());
    }
}
