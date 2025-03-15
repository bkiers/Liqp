package liqp.filters.date.fuzzy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.ZonedDateTime;
import org.junit.Test;

/**
 * This class is java reimplementation of the <a href="https://github.com/ruby/date/blob/master/test/date/test_date_parse.rb">tests from ruby Date.parse</a>
 * as a source of good collection of date formats to be tested against the FuzzyDateParser.
 */
// worknotes: https://chatgpt.com/c/67d4ecb4-38b0-8003-b2a3-797b9679af21
public class RubyDateParseTest {
    
    @Test
    public void test__parse() {
        Object[][] testCases = {
                //      # ctime(3), asctime(3)
                { "Sat Aug 28 02:55:50 1999", new Object[]{1999, 8, 28, 2, 55, 50, null, null, 6} },
                { "Sat Aug 28 02:55:50 02", new Object[]{2002, 8, 28, 2, 55, 50, null, null, 6} },
                { "Sat Aug 28 02:55:50 0002", new Object[]{2, 8, 28, 2, 55, 50, null, null, 6} },

                // # date(1)
                { "Sat Aug 28 02:29:34 JST 1999", new Object[]{1999, 8, 28, 2, 29, 34, "JST", 9*3600, 6} },
                { "Sat Aug 28 02:29:34 MET DST 1999", new Object[]{1999, 8, 28, 2, 29, 34, "MET DST", 2*3600, 6} },
                { "Sat Aug 28 02:29:34 AMT 1999", new Object[]{1999, 8, 28, 2, 29, 34, "AMT", null, 6} },
                { "Sat Aug 28 02:29:34 PMT 1999", new Object[]{1999, 8, 28, 2, 29, 34, "PMT", null, 6} },
                { "Sat Aug 28 02:29:34 PMT -1999", new Object[]{-1999, 8, 28, 2, 29, 34, "PMT", null, 6} },
        };

        FuzzyDateParser parser = new FuzzyDateParser();
        for (Object[] testCase : testCases) {
            ZonedDateTime parsed = parser.parse(testCase[0].toString(), null, null);
            assertNotNull("[" + testCase[0].toString() +"] parsed to null", parsed);

            Object[] expected = (Object[]) testCase[1];
            if (expected[0] != null) {
                assertEquals(testCase[0].toString(), (int) expected[0], parsed.getYear());
            }
            if (expected[1] != null) {
                assertEquals(testCase[0].toString(), (int) expected[1], parsed.getMonthValue());
            }
            if (expected[2] != null) {
                assertEquals(testCase[0].toString(), (int) expected[2], parsed.getDayOfMonth());
            }
            if (expected[3] != null) {
                assertEquals(testCase[0].toString(), (int) expected[3], parsed.getHour());
            }
            if (expected[4] != null) {
                assertEquals(testCase[0].toString(), (int) expected[4], parsed.getMinute());
            }
            if (expected[5] != null) {
                assertEquals(testCase[0].toString(), (int) expected[5], parsed.getSecond());
            }
        }

    }


}
