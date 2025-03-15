package liqp.filters.date.fuzzy;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class FuzzyDateParserParametrizedTest {

    private final String input;
    private final String expectedPattern;
    private final String expectedRender;
    private final Locale locale;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
//                {null, "1995", "yyyy", null},
//                {null, " 1995 ", " yyyy ", null},
//                {null, " 1995", " yyyy", null},
//                {null, "1995  ", "yyyy  ", null},
//                {null, "January 1995", "MMMM yyyy", null},
//                {null, "January 1995 ", "MMMM yyyy ", null},
//                {null, " January  1995", " MMMM  yyyy", null},
//                {null, "  1995   January", "  yyyy   MMMM", null},
//                {null, "Jan 1995", "MMM yyyy", null},
//                {null, "1995    Jan ", "yyyy    MMM ", null},
//                {Locale.GERMAN, "1995    Mai", "yyyy    MMMM", null},
//                FuzzyDateParser.CLDR_LOADED ?
//                        new Object[]{
//                            Locale.GERMAN, "??1995-----Dez.!", "'??'yyyy-----MMM'!'", null}
//                        : new Object[]{
//                            Locale.GERMAN, "??1995-----Dez!", "'??'yyyy-----MMM'!'", null}
//                ,
//                {null, "1:23", "H:mm", null},
//                {null, "01:23", "HH:mm", null},
//                {null, "1:23:45", "H:mm:ss", null},
//                {null, "01:23:45", "HH:mm:ss", null},
//                {null, "1:23:45.6", "H:mm:ss.S", null},
//                {null, "01:23:45.6", "HH:mm:ss.S", null},
//                {null, "1:23:45.67", "H:mm:ss.SS", null},
//                {null, "1:23:45.678", "H:mm:ss.SSS", null},
//                {null, "1:23:45.6789", "H:mm:ss.SSSS", null},
//                {null, "1:23:45.67890", "H:mm:ss.SSSSS", null},
//                {null, "1:23:45.678901", "H:mm:ss.SSSSSS", null},
//                {null, "1:23:45.6789012", "H:mm:ss.SSSSSSS", null},
//                {null, "1:23:45.67890123", "H:mm:ss.SSSSSSSS", null},
//                {null, "1:23:45.678901234", "H:mm:ss.SSSSSSSSS", null},
//                {null, "1:23:45.678901234AM", "h:mm:ss.SSSSSSSSSa", null}, // correct
//                {null, "1:23:45.678901234A", "H:mm:ss.SSSSSSSSS'A'", null},  // incorrect
//                {null, "1:23:45.678901234P", "H:mm:ss.SSSSSSSSS'P'", null},  // incorrect
//                {null, "1:23:45.678901234PM", "h:mm:ss.SSSSSSSSSa", null}, // correct
//                {null, "1:23:45.678901234   PM", "h:mm:ss.SSSSSSSSS   a", null}, // correct
//                {null, " 1:23:45.678", " H:mm:ss.SSS", null},
//                {null, " 1:23:45.678  ", " H:mm:ss.SSS  ", null},
//                {null, " 01:23:45.678  ", " HH:mm:ss.SSS  ", null},
//                {null, " 1:23:45.678 AM ", " h:mm:ss.SSS a ", null},
//                {null, " 1:23:45.678    PM ", " h:mm:ss.SSS    a ", null},
//                {null, "12 Jan 1995T01:23:45.678", "dd MMM yyyy'T'HH:mm:ss.SSS", null},
//                {null, "12  AD", "y  GG", null},
//                {null, " 12  AD ", " y  GG ", null},
//                {null, " 12  Anno Domini  ", " y  GGGG  ", null},
//                {null, " 12345  Before Christ  ", " y  GGGG  ", " 12345  Anno Domini  "},
//                {null, " 1  BC  ", " y  GG  ", " 1  AD  "},
//                {null, "12 January", "dd MMMM", null},
//                {null, " 12  January  ", " dd  MMMM  ", null},
//                {null, "12 Jan", "dd MMM", null},
//                {null, " 12  Jan  ", " dd  MMM  ", null},

                {null, " 12  BC  12 Jan 01:23:45.678 ", " y  GG  dd MMM HH:mm:ss.SSS ", null},
//                {null, "12 Jan 01:23:45.678  12  Anno Domini", "dd MMM HH:mm:ss.SSS  y  GGGG", null},
//                {null, "Monday", "EEEE", null},
//                {null, " Monday ", " EEEE ", null},
//                {null, "Monday  ", "EEEE  ", null},
//                {null, "  Monday", "  EEEE", null},
//                {null, "Mon", "EEE", null},
//                {null, " Mon ", " EEE ", null},
//                {null, " Mon", " EEE", null},
//                {null, "Mon  ", "EEE  ", null},
//                {Locale.GERMAN, "Montag", "EEEE", null},
//                {Locale.GERMAN, " Montag ", " EEEE ", null},
//                {Locale.GERMAN, "Montag  ", "EEEE  ", null},
//                {Locale.GERMAN, "  Montag", "  EEEE", null},
//                FuzzyDateParser.CLDR_LOADED ?
//                        new Object[]{
//                                Locale.GERMAN, "Mo.", "EEE", null}
//                        : new Object[]{
//                                Locale.GERMAN, "Mo", "EEE", null}
//                ,
//                {null, "Tuesday 31st December 2024 AD at 12:34:56.000 AM", "EEEE dd MMMM yyyy GG 'at' h:mm:ss.SSS a", null},
//                {null, "2021-1-2", "yyyy-M-d", null},
//                {null, "2021-01-2", "yyyy-MM-d", null},
//                {null, "2021-1-02", "yyyy-M-dd", null},
//                {null, "2024-1-5 08:15 ", "yyyy-M-d HH:mm ", null},
//                {null, "2024-12-25 14:45 ", "yyyy-MM-dd HH:mm ", null},
//                {null, "2024-12-25 14:45:30 ", "yyyy-MM-dd HH:mm:ss ", null},
//                {null, "1/1/23 ", "M/d/yy ", null},
//                {null, "1/1/2023 ", "M/d/yyyy ", null},
//                {null, "01/01/23 ", "MM/dd/yy ", null},
//                {null, "01/01/2023 ", "MM/dd/yyyy ", null},
//                {null, "1/1/23 12:34 ", "M/d/yy HH:mm ", null},
//                {null, "1/1/2023 12:34 ", "M/d/yyyy HH:mm ", null},
//                {null, "01/01/23 12:34 ", "MM/dd/yy HH:mm ", null},
//                {null, "11 31st   of  December  1996 ", "'11' dd   'of'  MMMM  yyyy ", null},
//                {null, "December.31st", "MMMM.dd", null},
//                {null, " 11 December, 10 ", " dd MMMM, y ", " 11 December, 2010 "},
//                {null, " 11, December 10 ", " y, MMMM dd ", " 2011, December 10 "},
//                {null, " 11 December - 10 ", " dd MMMM - y ", " 11 December - 2010 "},
//                {null, " 11 - December 10 ", " y - MMMM dd ", " 2011 - December 10 "},
//                {null, " 11 , December - 10 ", " y , MMMM - dd ", " 2011 , December - 10 "},
//                {null, " 11 - December , 10 ", " dd - MMMM , y ", " 11 - December , 2010 "},
//                {null, "11 December, 10", "dd MMMM, y", "11 December, 2010"},
//                {null, "11, December 10", "y, MMMM dd", "2011, December 10"},
//                {null, "11 December - 10", "dd MMMM - y", "11 December - 2010"},
//                {null, "11 - December 10", "y - MMMM dd", "2011 - December 10"},
//                {null, "11 , December - 10", "y , MMMM - dd", "2011 , December - 10"},
//                {null, "11 - December , 10", "dd - MMMM , y", "11 - December , 2010"},
//                {null, " 01/03 ", " MM/dd ", null},

        });
    }

    public FuzzyDateParserParametrizedTest(Locale locale, String input, String expectedPattern, String expectedRender) {
        this.locale = locale == null ? Locale.ENGLISH : locale;
        this.input = FuzzyDateParser.removeSequentialSuffixes(input);
        this.expectedPattern = expectedPattern;

        this.expectedRender = expectedRender;
    }

    @Test
    public void shouldParse() {
        ZonedDateTime parsed = null;
        String pattern = null;
        try {
            final FuzzyDateParser parser = new FuzzyDateParser();
            pattern = parser.guessPattern(input, locale, null).pattern;
            assertEquals(String.format("input is: [%s], expected pattern: [%s], real pattern: [%s]", input, expectedPattern, pattern), expectedPattern, pattern);
            parsed = parser.parse(input, locale, null);
            String formatted = parsed.format(DateTimeFormatter.ofPattern(pattern, locale));
            if (expectedRender != null) {
                assertEquals(String.format("input is: [%s], expected pattern: [%s], real pattern: [%s], real date: [%s]", input, expectedPattern, pattern, parsed),
                        expectedRender, formatted);
            } else {
                assertEquals(String.format("input is: [%s], expected pattern: [%s], real pattern: [%s], real date: [%s]", input, expectedPattern, pattern, parsed),
                        input, formatted);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("input is: [%s], expected pattern: [%s], real pattern: [%s], real date: [%s]", input, expectedPattern, pattern, parsed), e);
        }
    }

}
