package liqp.filters.date;

import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class FuzzyDateParserTest {
    private final String input;
    private final String expectedPattern;
    private final Locale locale;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {null,  "1995", "yyyy" },
                {null,  " 1995 ", " yyyy "},
                {null,  " 1995", " yyyy"},
                {null,  "1995  ", "yyyy  "},
                {null, "January 1995", "MMMM yyyy"},
                {null, "January 1995 ", "MMMM yyyy "},
                {null, " January  1995", " MMMM  yyyy"},
                {null, "  1995   January", "  yyyy   MMMM"},
                {null, "Jan 1995", "MMM yyyy"},
                {null, "1995    Jan ", "yyyy    MMM "},
                {Locale.GERMAN, "1995    Mai", "yyyy    MMMM"},
                {Locale.GERMAN, "??1995-----Dez!", "??yyyy-----MMM!"},
        });
    }

    public FuzzyDateParserTest(Locale locale, String input, String expectedPattern) {
        this.locale = locale;
        this.input = input;
        this.expectedPattern = expectedPattern;
    }

    @Test
    public void shouldParse() {
        final FuzzyDateParser parser = new FuzzyDateParser();
        String pattern = parser.guessPattern(input, locale);
        assertEquals(expectedPattern, pattern);
    }

}
