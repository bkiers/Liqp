package liqp.filters.date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class FuzzyDateParserTest {
    private FuzzyDateParser parser = new FuzzyDateParser();

    private final String input;
    private final String expectedPattern;



    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "1995", "yyyy" },
                { " 1995 ", " yyyy "},
                { " 1995", " yyyy"},
                { "1995  ", "yyyy  "},
        });
    }

    public FuzzyDateParserTest(String input, String expectedPattern) {
        this.input = input;
        this.expectedPattern = expectedPattern;
    }

    @Test
    public void shouldParse() {
        String pattern = parser.guessPattern(input);
        assertEquals(expectedPattern, pattern);
    }

}