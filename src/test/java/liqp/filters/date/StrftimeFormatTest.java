package liqp.filters.date;

import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class StrftimeFormatTest {

    @Test
    public void testTimeZoneHourOffsetStrftimeFormat() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("XXXX", Locale.ENGLISH);
        // New York timezone with winter time, negative offset
        String val = format.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.of("America/New_York")));
        assertEquals("-0500", val);

        // New York fixed(winter) timezone, negative offset
        val = format.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.of("EST", ZoneId.SHORT_IDS)));
        assertEquals("-0500", val);

        // new York timezone with summer time, negative offset
        ZonedDateTime summer1970 = ZonedDateTime.of(LocalDateTime.of(2070, 5, 15, 0, 0), ZoneId.of("America/New_York"));
        val = format.format(summer1970);
        assertEquals("-0400", val);

        // Kyiv winter time, positive offset
        val = format.format(ZonedDateTime.ofInstant(Instant.ofEpochSecond(0), ZoneId.of("EET", ZoneId.SHORT_IDS)));
        assertEquals("+0200", val);

        // India time, positive offset with minutes
        val = format.format(ZonedDateTime.ofInstant(Instant.ofEpochSecond(0), ZoneId.of("IST", ZoneId.SHORT_IDS)));
        assertEquals("+0530", val);
    }

}
