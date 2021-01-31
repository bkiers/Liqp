package liqp.filters.date;

import liqp.RenderSettings;
import liqp.Template;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class StrftimeCompatibleDateTypeSupportTest {
    @Test
    public void testCase() {
        // 2021-11-03 16:40:44 Pacific Standard Time
        // DST starts - 14 Mar 2021 (Forward 1 hour)
        // DST ends - 7 Nov 2021 (Back 1 hour)
        Object val = ZonedDateTime.of(LocalDateTime.of(2021, 11, 3, 16, 40, 44), ZoneId.of("America/Los_Angeles"));

        String res = Template.parse("{{ val | date: '%Y-%m-%d %H:%M:%S %Z' }}")
                .render(Collections.singletonMap("val", val));
        assertEquals("2021-11-03 16:40:44 Pacific Daylight Time", res);

        res = Template.parse("{{ val | date: '%Y-%m-%d %H:%M:%S %Z' }}").withRenderSettings(new RenderSettings
                .Builder()
                .withLocale(Locale.GERMANY)
                .build())
                .render(Collections.singletonMap("val", val));

        int classVersion = Double.valueOf(System.getProperty("java.class.version")).intValue();
        // JDK localisation changed text starting 9th java version, its 53 java class version
        if (classVersion > 52) {
            assertEquals("2021-11-03 16:40:44 Nordamerikanische Westküsten-Sommerzeit", res);
        } else {
            assertEquals("2021-11-03 16:40:44 Pazifische Sommerzeit", res);
        }
    }

}
