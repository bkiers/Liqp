package liqp.filters.date;

import liqp.filters.date.StrftimeFormat.TimeZoneHourOffsetStrftimeFormat;
import org.junit.Test;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class StrftimeFormatTest {

    @Test
    public void testTimeZoneHourOffsetStrftimeFormat() {
        TimeZoneHourOffsetStrftimeFormat format = new TimeZoneHourOffsetStrftimeFormat(Locale.ENGLISH);
        // New York timezone with winter time, negative offset
        String val = format.format(new StrftimeCompatibleDate(0, TimeZone.getTimeZone("America/New_York")));
        assertEquals("-0500", val);

        // New York fixed(winter) timezone, negative offset
        format.format(new StrftimeCompatibleDate(0, TimeZone.getTimeZone("EST")));
        assertEquals("-0500", val);

        // new York timezone with summer time, negative offset
        Date summer1970 = new Date(70, 5, 15);
        val = format.format(new StrftimeCompatibleDate(summer1970.getTime(), TimeZone.getTimeZone("America/New_York")));
        assertEquals("-0400", val);

        // Kyiv winter time, positive offset
        val = format.format(new StrftimeCompatibleDate(0, TimeZone.getTimeZone("EET")));
        assertEquals("+0200", val);

        // India time, positive offset with minutes
        val = format.format(new StrftimeCompatibleDate(0, TimeZone.getTimeZone("IST")));
        assertEquals("+0530", val);

        // Impossible case for pre java8 world, as timezones are managed by Calendar,
        // and that one return either defined, either default.
        // But next java versions have that (example: LocalDateTime), so must be handled properly
        // according to strftime convention: if some field is not accessible it is omitted
        val = format.format(new StrftimeCompatibleDate(0, null));
        assertEquals("", val);
    }

}
