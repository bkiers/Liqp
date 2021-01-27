package liqp.filters.date;

import liqp.RenderSettings;
import liqp.Template;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class StrftimeCompatibleDateTypeSupportTest {
    @Test
    public void testCase() {
        // 2020-11-03 16:40:44 Pacific Standard Time
        long currentTime = new Date(120, Calendar.NOVEMBER, 3, 16, 40, 44).getTime();
        long timeWithoutTimeZone = currentTime + TimeZone.getDefault().getOffset(currentTime) - TimeZone.getTimeZone("PST").getOffset(currentTime);
        Object val = new StrftimeCompatibleDate(timeWithoutTimeZone, TimeZone.getTimeZone("PST").getID());

        String res = Template.parse("{{ val | date: '%Y-%m-%d %H:%M:%S %Z' }}")
                .render(Collections.singletonMap("val", val));
        assertEquals("2020-11-03 16:40:44 Pacific Standard Time", res);

        res = Template.parse("{{ val | date: '%Y-%m-%d %H:%M:%S %Z' }}").withRenderSettings(new RenderSettings
                .Builder()
                .withLocale(Locale.GERMANY)
                .build())
                .render(Collections.singletonMap("val", val));
        assertEquals("2020-11-03 16:40:44 Pazifische Normalzeit", res);

    }

}
