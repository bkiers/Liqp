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
                {null, "1:23:45.678901234am", "h:mm:ss.SSSSSSSSSa"}, // correct
                {null, "1:23:45.678901234a", "H:mm:ss.SSSSSSSSS'a'"},  // incorrect
                {null, "1:23:45.678901234p", "H:mm:ss.SSSSSSSSS'p'"},  // incorrect
                {null, "1:23:45.678901234pm", "h:mm:ss.SSSSSSSSSa"}, // correct
                {null, "1:23:45.678901234   pm", "h:mm:ss.SSSSSSSSS   a"}, // correct
                {null, " 1:23:45.678", " H:mm:ss.SSS"},
                {null, " 1:23:45.678  ", " H:mm:ss.SSS  "},
                {null, " 01:23:45.678  ", " HH:mm:ss.SSS  "},
                {null, " 1:23:45.678 am ", " h:mm:ss.SSS a "},
                {null, " 1:23:45.678    PM ", " h:mm:ss.SSS    a "},
                {null, "12 Jan 1995T01:23:45.678", "'12' MMM yyyyTHH:mm:ss.SSS"},
                {null, "12  AD", "yyyy  GG"},
                {null, " 12  AD ", " yyyy  GG "},
                {null, " 12  Anno Domini  ", " yyyy  GGGG  "},
                {null, " 12345  Before Christ  ", " yyyy  GGGG  "},
                {null, " 0  BC  ", " yyyy  GG  "},
                {null, "12 January", "'12' MMMM"},
                {null, " 12  January  ", " '12'  MMMM  "},
                {null, "12 Jan", "'12' MMM"},
                {null, " 12  Jan  ", " '12'  MMM  "},

                {null, " 12  BC  12 Jan 01:23:45.678 ", " yyyy  GG  '12' MMM HH:mm:ss.SSS "},
                {null, "12 Jan 01:23:45.678  12  Anno Domini", "'12' MMM HH:mm:ss.SSS  yyyy  GGGG"},
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
                {null, "Monday 17th September 1999 BC at 12:34:56.000 AM", "EEEE '17th' MMMM yyyy GG 'at' h:mm:ss.SSS a"},
                {null, "2021-11-23", "yyyy-MM-dd"},
                {null, "2024-1-5 08:15 ", "yyyy-MM-d HH:mm"},
                {null, "2024-12-25 14:45 ", "yyyy-MM-d HH:mm"},

        });
    }

    public FuzzyDateParserParametrizedTest(Locale locale, String input, String expectedPattern) {
        this.locale = locale == null ? Locale.ENGLISH : locale;
        this.input = input;
        this.expectedPattern = expectedPattern;
    }

    @Test
    public void shouldParse() {
        ZonedDateTime parsed = null;
        String pattern = null;
        try {
            final FuzzyDateParser parser = new FuzzyDateParser();
            pattern = parser.guessPattern(input, locale);
            assertEquals(expectedPattern, pattern);
            parsed = parser.parse(input, locale, null);
            String formatted = parsed.format(DateTimeFormatter.ofPattern(pattern, locale)).toLowerCase(locale);
            assertEquals(String.format("input is: [%s], expected pattern: [%s], real pattern: [%s], real date: [%s]", input, expectedPattern, pattern, parsed),
                    input.toLowerCase(locale), formatted);
        } catch (Exception e) {
            throw new RuntimeException(String.format("input is: [%s], expected pattern: [%s], real pattern: [%s], real date: [%s]", input, expectedPattern, pattern, parsed), e);
        }
    }


}
