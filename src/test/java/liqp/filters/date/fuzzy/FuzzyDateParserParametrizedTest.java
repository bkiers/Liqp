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
    private final Locale locale;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {null, "1995", "yyyy"},
                {null, " 1995 ", " yyyy "},
                {null, " 1995", " yyyy"},
                {null, "1995  ", "yyyy  "},
                {null, "January 1995", "MMMM yyyy"},
                {null, "January 1995 ", "MMMM yyyy "},
                {null, " January  1995", " MMMM  yyyy"},
                {null, "  1995   January", "  yyyy   MMMM"},
                {null, "Jan 1995", "MMM yyyy"},
                {null, "1995    Jan ", "yyyy    MMM "},
                {Locale.GERMAN, "1995    Mai", "yyyy    MMMM"},
                FuzzyDateParser.CLDR_LOADED ?
                        new Object[]{
                            Locale.GERMAN, "??1995-----Dez.!", "'??'yyyy-----MMM'!'"}
                        : new Object[]{
                            Locale.GERMAN, "??1995-----Dez!", "'??'yyyy-----MMM'!'"}
                ,
                {null, "1:23", "H:mm"},
                {null, "01:23", "HH:mm"},
                {null, "1:23:45", "H:mm:ss"},
                {null, "01:23:45", "HH:mm:ss"},
                {null, "1:23:45.6", "H:mm:ss.S"},
                {null, "01:23:45.6", "HH:mm:ss.S"},
                {null, "1:23:45.67", "H:mm:ss.SS"},
                {null, "1:23:45.678", "H:mm:ss.SSS"},
                {null, "1:23:45.6789", "H:mm:ss.SSSS"},
                {null, "1:23:45.67890", "H:mm:ss.SSSSS"},
                {null, "1:23:45.678901", "H:mm:ss.SSSSSS"},
                {null, "1:23:45.6789012", "H:mm:ss.SSSSSSS"},
                {null, "1:23:45.67890123", "H:mm:ss.SSSSSSSS"},
                {null, "1:23:45.678901234", "H:mm:ss.SSSSSSSSS"},
                {null, "1:23:45.678901234AM", "h:mm:ss.SSSSSSSSSa"}, // correct
                {null, "1:23:45.678901234A", "H:mm:ss.SSSSSSSSS'A'"},  // incorrect
                {null, "1:23:45.678901234P", "H:mm:ss.SSSSSSSSS'P'"},  // incorrect
                {null, "1:23:45.678901234PM", "h:mm:ss.SSSSSSSSSa"}, // correct
                {null, "1:23:45.678901234   PM", "h:mm:ss.SSSSSSSSS   a"}, // correct
                {null, " 1:23:45.678", " H:mm:ss.SSS"},
                {null, " 1:23:45.678  ", " H:mm:ss.SSS  "},
                {null, " 01:23:45.678  ", " HH:mm:ss.SSS  "},
                {null, " 1:23:45.678 AM ", " h:mm:ss.SSS a "},
                {null, " 1:23:45.678    PM ", " h:mm:ss.SSS    a "},
                {null, "12 Jan 1995T01:23:45.678", "dd MMM yyyy'T'HH:mm:ss.SSS"},
                {null, "12  AD", "y  GG"},
                {null, " 12  AD ", " y  GG "},
                {null, " 12  Anno Domini  ", " y  GGGG  "},
                {null, " 12345  Before Christ  ", " y  GGGG  "},
                {null, " 1  BC  ", " y  GG  "},
                {null, "12 January", "dd MMMM"},
                {null, " 12  January  ", " dd  MMMM  "},
                {null, "12 Jan", "dd MMM"},
                {null, " 12  Jan  ", " dd  MMM  "},

                {null, " 12  BC  12 Jan 01:23:45.678 ", " y  GG  dd MMM HH:mm:ss.SSS "},
                {null, "12 Jan 01:23:45.678  12  Anno Domini", "dd MMM HH:mm:ss.SSS  y  GGGG"},
                {null, "Monday", "EEEE"},
                {null, " Monday ", " EEEE "},
                {null, "Monday  ", "EEEE  "},
                {null, "  Monday", "  EEEE"},
                {null, "Mon", "EEE"},
                {null, " Mon ", " EEE "},
                {null, " Mon", " EEE"},
                {null, "Mon  ", "EEE  "},
                {Locale.GERMAN, "Montag", "EEEE"},
                {Locale.GERMAN, " Montag ", " EEEE "},
                {Locale.GERMAN, "Montag  ", "EEEE  "},
                {Locale.GERMAN, "  Montag", "  EEEE"},
                FuzzyDateParser.CLDR_LOADED ?
                        new Object[]{
                                Locale.GERMAN, "Mo.", "EEE"}
                        : new Object[]{
                                Locale.GERMAN, "Mo", "EEE"}
                ,
                {null, "Tuesday 31st December 2024 AD at 12:34:56.000 AM", "EEEE dd MMMM yyyy GG 'at' h:mm:ss.SSS a"},
                {null, "2021-1-2", "yyyy-M-d"},
                {null, "2021-01-2", "yyyy-MM-d"},
                {null, "2021-1-02", "yyyy-M-dd"},
                {null, "2024-1-5 08:15 ", "yyyy-M-d HH:mm "},
                {null, "2024-12-25 14:45 ", "yyyy-MM-dd HH:mm "},
                {null, "2024-12-25 14:45:30 ", "yyyy-MM-dd HH:mm:ss "},
                {null, "1/1/23 ", "M/d/yy "},
                {null, "1/1/2023 ", "M/d/yyyy "},
                {null, "01/01/23 ", "MM/dd/yy "},
                {null, "01/01/2023 ", "MM/dd/yyyy "},
                {null, "1/1/23 12:34 ", "M/d/yy HH:mm "},
                {null, "1/1/2023 12:34 ", "M/d/yyyy HH:mm "},
                {null, "01/01/23 12:34 ", "MM/dd/yy HH:mm "},
                {null, "11 31st   of  December  1996 ", "'11' dd   'of'  MMMM  yyyy "},
                {null, "December.31st", "MMMM.dd"},
                {null, " 11 December, 11 ", " dd MMMM, '11' "},
                {null, " 11, December 11 ", " '11', MMMM dd "},
                {null, " 11 December - 11 ", " dd MMMM - '11' "},
                {null, " 11 - December 11 ", " '11' - MMMM dd "},
                {null, " 11 , December - 11 ", " '11' , MMMM - dd "},
                {null, " 11 - December , 11 ", " dd - MMMM , '11' "},
                {null, "11 December, 11", "dd MMMM, '11'"},
                {null, "11, December 11", "'11', MMMM dd"},
                {null, "11 December - 11", "dd MMMM - '11'"},
                {null, "11 - December 11", "'11' - MMMM dd"},
                {null, "11 , December - 11", "'11' , MMMM - dd"},
                {null, "11 - December , 11", "dd - MMMM , '11'"},

                {null, "December - 11", "MMMM - dd"}

        });
    }

    public FuzzyDateParserParametrizedTest(Locale locale, String input, String expectedPattern) {
        this.locale = locale == null ? Locale.ENGLISH : locale;
        this.input = FuzzyDateParser.removeSequentialSuffixes(input);
        this.expectedPattern = expectedPattern;
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
            assertEquals(String.format("input is: [%s], expected pattern: [%s], real pattern: [%s], real date: [%s]", input, expectedPattern, pattern, parsed),
                    input, formatted);
        } catch (Exception e) {
            throw new RuntimeException(String.format("input is: [%s], expected pattern: [%s], real pattern: [%s], real date: [%s]", input, expectedPattern, pattern, parsed), e);
        }
    }

}
