package liqp.filters.date.fuzzy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import liqp.filters.date.fuzzy.extractors.Extractors;
import liqp.filters.date.fuzzy.extractors.PartExtractorResult;
import org.junit.Test;

public class FuzzyDateParserTest {
    @Test
    public void testTimeRegexp() {
        PartExtractor partExtractor = Extractors.regularTimeExtractor.get(null);
        PartExtractorResult result = partExtractor.extract(" 12:34 ", null, -1);
        assertTrue(result.found);
        assertEquals( 1, result.start);
        assertEquals( 6, result.end);
        assertEquals("HH:mm", result.formatterPatterns.get(0));
    }

    @Test
    public void testSelfCorrectLowToBig() {
        unchecked(() -> Class.forName(FuzzyDateParser.class.getName()));
        final FuzzyDateParser parser = new FuzzyDateParser();
        ZonedDateTime parsed = parser.parse("1:00 am", null, null);
        assertEquals("01:00:00", parsed.format(DateTimeFormatter.ISO_LOCAL_TIME));
        parsed = parser.parse("01:00 am", null, null);
        assertEquals("01:00:00", parsed.format(DateTimeFormatter.ISO_LOCAL_TIME));
    }

    @Test
    public void testSelfCorrectBigToLow() {
        unchecked(() -> Class.forName(FuzzyDateParser.class.getName()));
        final FuzzyDateParser parser = new FuzzyDateParser();
        ZonedDateTime parsed = parser.parse("01:00 am", null, null);
        assertEquals("01:00:00", parsed.format(DateTimeFormatter.ISO_LOCAL_TIME));
        parsed = parser.parse("1:00 am", null, null);
        assertEquals("01:00:00", parsed.format(DateTimeFormatter.ISO_LOCAL_TIME));
    }

    static void unchecked(@SuppressWarnings("rawtypes") Callable f) {
        try {
            f.call();
        } catch (Exception ignored) { }
    }
}
