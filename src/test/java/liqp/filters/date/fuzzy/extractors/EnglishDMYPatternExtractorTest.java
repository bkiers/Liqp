package liqp.filters.date.fuzzy.extractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Locale;
import org.junit.Test;

public class EnglishDMYPatternExtractorTest{
    @Test
    public void test() {
        EnglishDMYPatternExtractor extractor = new EnglishDMYPatternExtractor();
        PartExtractorResult extract = extractor.extract("  1/1/11   ");
        assertTrue(extract.found);
        assertEquals(2, extract.start);
        assertEquals(8, extract.end);
        assertEquals(4, extract.formatterPatterns.size());
        assertEquals(Arrays.asList("d/M/yy", "d/MM/yy", "dd/M/yy", "dd/MM/yy"),
                extract.formatterPatterns);
    }

    @Test
    public void test2() {
        EnglishDMYPatternExtractor extractor = new EnglishDMYPatternExtractor();
        PartExtractorResult extract = extractor.extract("  31/12/11   ");
        assertTrue(extract.found);
        assertEquals(2, extract.start);
        assertEquals(10, extract.end);
        assertEquals(4, extract.formatterPatterns.size());
        // order matters!
        assertEquals(Arrays.asList("dd/MM/yy", "dd/M/yy", "d/MM/yy", "d/M/yy"),
                extract.formatterPatterns);
    }
}