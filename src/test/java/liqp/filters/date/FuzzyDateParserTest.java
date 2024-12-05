package liqp.filters.date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import liqp.filters.date.FuzzyDateParser.PartExtractor;
import liqp.filters.date.FuzzyDateParser.PartExtractorResult;
import org.junit.Test;

public class FuzzyDateParserTest {
    @Test
    public void testTimeRegexp() {
        PartExtractor partExtractor = FuzzyDateParser.regularTimeExtractor;
        PartExtractorResult result = partExtractor.extract(" 12:34 ");
        assertTrue(result.found);
        assertEquals( 1, result.start);
        assertEquals( 6, result.end);
        assertEquals(result.formatterPattern, "HH:mm");
    }
}
