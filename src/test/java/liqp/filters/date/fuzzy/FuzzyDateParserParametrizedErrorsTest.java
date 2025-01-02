package liqp.filters.date.fuzzy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class FuzzyDateParserParametrizedErrorsTest {

    private final String input;
    private final Class exceptionClass;
    private final Locale locale;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {null, " 11 december 11", RuntimeException.class},
                {null, " 11 -december- 11", RuntimeException.class},
        });
    }

    public FuzzyDateParserParametrizedErrorsTest(Locale locale, String input, Class exceptionClass) {
        this.locale = locale == null ? Locale.ENGLISH : locale;
        this.input = FuzzyDateParser.removeSequentialSuffixes(input);
        this.exceptionClass = exceptionClass;
    }

    @Test
    public void shouldParse() {
        try {
            final FuzzyDateParser parser = new FuzzyDateParser();
            parser.guessPattern(input, locale, null);
            fail(String.format("input is: [%s] and should be wrong", input));
        } catch (Exception e) {
            if (!exceptionClass.isInstance(e)) {
                fail(String.format("for input %s exception class should be %s, but it was %s instead", input, exceptionClass, e.getClass()));
            }
        }
    }


}
