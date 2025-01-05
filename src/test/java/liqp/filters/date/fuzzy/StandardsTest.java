package liqp.filters.date.fuzzy;

import static org.junit.Assert.assertEquals;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class StandardsTest {

    /**
     * RFC 822 (superseded by RFC 1123 because of Y2K)
     * Full or abbreviated weekday name
     * Day must be 2 digits
     * Month must be 3-letter abbreviation
     * 2-digit year (YY) or 4-digit year (YYYY)
     * Hour must have a leading zero, still it may be without
     * Time and timezone are required
     * Timezone offset in ±hhmm or abbreviated names (e.g., GMT, EST, PST).
     */
    @Test
    public void testRFC822() {
        FuzzyDateParser parser = new FuzzyDateParser();
        String[] samplesWithSeconds = {
            "Sun, 06 Nov 1994 08:49:37 GMT",
            "Sun, 06 Nov 94 08:49:37 GMT",
            "Sun, 6 Nov 1994 08:49:37 GMT",
            "Sun, 6 Nov 94 08:49:37 GMT",
            "Sun, 06 Nov 1994 8:49:37 GMT",
            "Sun, 06 Nov 94 8:49:37 GMT",
            "Sun, 6 Nov 1994 8:49:37 GMT",
            "Sun, 6 Nov 94 8:49:37 GMT",
        };

        for (String sample : samplesWithSeconds) {
            ZonedDateTime datetime = parser.parse(sample, null, ZoneOffset.UTC);
            assertEquals("wrong sample:[" + sample + "]", ZonedDateTime.of(1994, 11, 6, 8, 49, 37, 0, ZoneOffset.UTC), datetime);
        }

        String[] samplesWithoutSeconds = {
                "Sun, 06 Nov 1994 08:49 GMT",
                "Sun, 06 Nov 94 08:49 GMT",
                "Sun, 6 Nov 1994 08:49 GMT",
                "Sun, 6 Nov 94 08:49 GMT",
                "Sun, 06 Nov 1994 8:49 GMT",
                "Sun, 06 Nov 94 8:49 GMT",
                "Sun, 6 Nov 1994 8:49 GMT",
                "Sun, 6 Nov 94 8:49 GMT",
        };

        for (String sample : samplesWithoutSeconds) {
            ZonedDateTime datetime = parser.parse(sample, null, ZoneOffset.UTC);
            assertEquals("wrong sample:[" + sample + "]", ZonedDateTime.of(1994, 11, 6, 8, 49, 0, 0, ZoneOffset.UTC), datetime);
        }

        String[] samplesWithoutTime = {
                "Sun, 06 Nov 1994",
                "Sun, 06 Nov 94",
                "Sun, 6 Nov 1994",
                "Sun, 6 Nov 94"
        };

        for (String sample : samplesWithoutTime) {
            ZonedDateTime datetime = parser.parse(sample, null, ZoneOffset.UTC);
            assertEquals("wrong sample:[" + sample + "]", ZonedDateTime.of(1994, 11, 6, 0, 0, 0, 0, ZoneOffset.UTC), datetime);
        }

    }

    /**
     * RFC 1123 (an update to RFC 822 for Internet date/time)
     * Same as RFC822 but with 4-digit year
     * Enforces a strict 2-digit day
     * Timezone must always be GMT (or UTC)
     */
    @Test
    public void testRFC1123() {
        FuzzyDateParser parser = new FuzzyDateParser();

        String[] samplesWithSeconds = {
            "Sun, 06 Nov 1994 08:49:37 GMT",
            "Sun, 6 Nov 1994 08:49:37 GMT",
            "Sun, 06 Nov 1994 8:49:37 GMT",
            "Sun, 6 Nov 1994 8:49:37 GMT"
        };

        for (String sample : samplesWithSeconds) {
            ZonedDateTime datetime = parser.parse(sample, null, ZoneOffset.UTC);
            assertEquals("wrong sample:[" + sample + "]", ZonedDateTime.of(1994, 11, 6, 8, 49, 37, 0, ZoneOffset.UTC), datetime);
        }

        String[] samplesWithoutSeconds = {
            "Sun, 06 Nov 1994 08:49 GMT",
            "Sun, 6 Nov 1994 08:49 GMT",
            "Sun, 06 Nov 1994 8:49 GMT",
            "Sun, 6 Nov 1994 8:49 GMT",
        };

        for (String sample : samplesWithoutSeconds) {
            ZonedDateTime datetime = parser.parse(sample, null, ZoneOffset.UTC);
            assertEquals("wrong sample:[" + sample + "]", ZonedDateTime.of(1994, 11, 6, 8, 49, 0, 0, ZoneOffset.UTC), datetime);
        }

        String[] samplesWithoutTime = {
            "Sun, 06 Nov 1994",
            "Sun, 6 Nov 1994",
        };

        for (String sample : samplesWithoutTime) {
            ZonedDateTime datetime = parser.parse(sample, null, ZoneOffset.UTC);
            assertEquals("wrong sample:[" + sample + "]", ZonedDateTime.of(1994, 11, 6, 0, 0, 0, 0, ZoneOffset.UTC), datetime);
        }

    }

    /**
     * RFC 2822 (email standard update)
     * Same as RFC822 but with 4-digit year
     * Day must be 2 digits
     * Hours and minutes must be 2 digits
     * Seconds are optional
     * Timezone required and must be ±hhmm
     */
    @Test
    public void testRFC2822() {
        String[] samplesWithSeconds = {
            "Sun, 06 Nov 1994 08:49:37 +0000",
            "Sun, 6 Nov 1994 08:49:37 +0000",
            "Sun, 06 Nov 1994 8:49:37 +0000",
            "Sun, 6 Nov 1994 8:49:37 +0000",
        };

        FuzzyDateParser parser = new FuzzyDateParser();
        for (String sample : samplesWithSeconds) {
            ZonedDateTime datetime = parser.parse(sample, null, ZoneOffset.UTC);
            assertEquals("wrong sample:[" + sample + "]", ZonedDateTime.of(1994, 11, 6, 8, 49, 37, 0, ZoneOffset.UTC), datetime);
        }

        String[] samplesWithoutSeconds = {
            "Sun, 06 Nov 1994 08:49 +0000",
            "Sun, 6 Nov 1994 08:49 +0000",
            "Sun, 06 Nov 1994 8:49 +0000",
            "Sun, 6 Nov 1994 8:49 +0000",
        };

        for (String sample : samplesWithoutSeconds) {
            ZonedDateTime datetime = parser.parse(sample, null, ZoneOffset.UTC);
            assertEquals("wrong sample:[" + sample + "]", ZonedDateTime.of(1994, 11, 6, 8, 49, 0, 0, ZoneOffset.UTC), datetime);
        }

        String[] samplesWithoutTime = {
            "Sun, 06 Nov 1994 +0000",
            "Sun, 6 Nov 1994 +0000",
        };

        for (String sample : samplesWithoutTime) {
            ZonedDateTime datetime = parser.parse(sample, null, ZoneOffset.UTC);
            assertEquals("wrong sample:[" + sample + "]", ZonedDateTime.of(1994, 11, 6, 0, 0, 0, 0, ZoneOffset.UTC), datetime);
        }
    }

    /**
     * RFC 3339 (ISO 8601 profile for Internet)
     * Example: 1997-11-21T09:55:06Z
     * Z is the timezone offset and is optional
     * Fractional seconds are optional
     * Instead of 'T' the space is allowed as separator (ISO 8601 requires 'T')
     * Separators are mandatory (ISO 8601 allows omission: 20201209T160953Z)
     * No ordinal dates allowed (ISO 8601 allows 2020-344)
     * No week dates allowed (ISO 8601 allows 2020-W52-3)
     * negative zero timezone offset allowed '-00:00' (same in ISO 8601)
     * Timezone offset must be 'Z' or ±hh:mm
     */
    @Test
    public void testRFC3339() {
        FuzzyDateParser parser = new FuzzyDateParser();
        String[] samplesWithMilliseconds = {
            "1994-11-06T08:49:37.123Z",
            "1994-11-06T08:49:37.123+00:00",
            "1994-11-06T08:49:37.123-00:00",
            "1994-11-06T08:49:37.123+0000",
            "1994-11-06T08:49:37.123-0000",
            "1994-11-06T08:49:37.123+00",
            "1994-11-06T08:49:37.123-00",
            "1994-11-06T08:49:37.123",
        };

        for (String sample : samplesWithMilliseconds) {
            ZonedDateTime datetime = parser.parse(sample, null, ZoneOffset.UTC);
            ZonedDateTime expected = ZonedDateTime.of(1994, 11, 6, 8, 49, 37,
                    ((int) TimeUnit.MILLISECONDS.toNanos(123L)), ZoneOffset.UTC);
            assertEquals("wrong sample:[" + sample + "]", expected, datetime);
        }

        String[] samplesWithSeconds = {
            "1994-11-06T08:49:37Z",
            "1994-11-06 08:49:37Z",
            "1994-11-06T08:49:37+0000",
            "1994-11-06T08:49:37+00",
            "1994-11-06T08:49:37+00:00",
            "1994-11-06T08:49:37"
        };

        for (String sample : samplesWithSeconds) {
            ZonedDateTime datetime = parser.parse(sample, null, ZoneOffset.UTC);
            ZonedDateTime expected = ZonedDateTime.of(1994, 11, 6, 8, 49, 37, 0, ZoneOffset.UTC);
            assertEquals("wrong sample:[" + sample + "]", expected, datetime);
        }

        String[] samplesWithoutSeconds = {
            "1994-11-06T08:49Z",
            "1994-11-06 08:49Z",
            "1994-11-06T08:49+0000",
            "1994-11-06T08:49+00",
            "1994-11-06T08:49+00:00",
            "1994-11-06T08:49",
        };

        for (String sample : samplesWithoutSeconds) {
            ZonedDateTime datetime = parser.parse(sample, null, ZoneOffset.UTC);
            assertEquals("wrong sample:[" + sample + "]", ZonedDateTime.of(1994, 11, 6, 8, 49, 0, 0, ZoneOffset.UTC), datetime);
        }

        String[] samplesWithoutTime = {
            "1994-11-06T08", // verified on ruby time parser - hours without minutes is not a "time"
            "1994-11-06",
            "1994-11-06Z",
            "1994-11-06",
        };

        for (String sample : samplesWithoutTime) {
            ZonedDateTime datetime = parser.parse(sample, null, ZoneOffset.UTC);
            assertEquals("wrong sample:[" + sample + "]", ZonedDateTime.of(1994, 11, 6, 0, 0, 0, 0, ZoneOffset.UTC), datetime);
        }
    }

    @Test
    public void testISO8601() {
        String[] samples = {
            "1994-11-06T08:49:37.123Z",
            "1994-11-06T08:49:37Z",
            "1994-11-06T08:49:37+00:00",
            "1994-11-06T08:49:37+0000",
            "1994-11-06T08:49:37+00",
            "1994-11-06T08:49:37",
            "1994-11-06T08:49",
            "1994-11-06T08",
            "1994-11-06",
        };
    }


    // more:
    // https://chatgpt.com/g/g-p-6762180fb09c81919a7acd1088c93256-liqp/c/676888d1-5bfc-8003-9854-8e9033aa608a

}
