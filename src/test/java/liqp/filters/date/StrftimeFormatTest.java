package liqp.filters.date;

import liqp.filters.date.StrftimeFormat.TimeZoneHourOffsetStrftimeFormat;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class StrftimeFormatTest {

    @Test
    public void testTimeZoneHourOffsetStrftimeFormat() {
        TimeZoneHourOffsetStrftimeFormat format = new TimeZoneHourOffsetStrftimeFormat(Locale.ENGLISH);
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

        // Impossible case for pre java8 world, as timezones are managed by Calendar,
        // and that one return either defined, either default.
        // But next java versions have that (example: LocalDateTime), so must be handled properly
        // according to strftime convention: if some field is not accessible it is omitted
        val = format.format(null);
        assertEquals("", val);
    }

}
