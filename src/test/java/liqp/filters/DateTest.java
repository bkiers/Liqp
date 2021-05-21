package liqp.filters;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Locale;
import liqp.Template;
import liqp.TemplateContext;
import liqp.filters.date.CustomDateFormatSupport;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class DateTest {

    @Before
    public void init() {
        // reset
        Filter.registerFilter(new Date());
    }

    @Test
    public void applyTest() throws RecognitionException {

        final int seconds = 946702800;
        // 1st Jan 2000 05:00:00 UTC
        // (if you are ot UTC, better not to use deprecated Date constructors)
        final java.util.Date date = new java.util.Date(seconds * 1000L);

        String[][] tests = {
                {"{{" + seconds + " | date: 'mu'}}", "mu"},
                {"{{" + seconds + " | date: '%'}}", "%"},
                {"{{" + seconds + " | date: '%? %%'}}", "%? %"},
                {"{{" + seconds + " | date: '%a'}}", simpleDateFormat("EEE").format(date)},
                {"{{" + seconds + " | date: '%A'}}", simpleDateFormat("EEEE").format(date)},
                {"{{" + seconds + " | date: '%b'}}", simpleDateFormat("MMM").format(date)},
                {"{{" + seconds + " | date: '%B'}}", simpleDateFormat("MMMM").format(date)},
                {"{{" + seconds + " | date: '%c'}}", simpleDateFormat("EEE MMM d HH:mm:ss yyyy").format(date)},
                {"{{" + seconds + " | date: '%d'}}", simpleDateFormat("dd").format(date)},
                {"{{" + seconds + " | date: '%e'}}", simpleDateFormat("d").format(date)},
                {"{{" + seconds + " | date: '%H'}}", simpleDateFormat("HH").format(date)},
                {"{{" + seconds + " | date: '%I'}}", simpleDateFormat("hh").format(date)},
                {"{{" + seconds + " | date: '%j'}}", simpleDateFormat("DDD").format(date)},
                {"{{" + seconds + " | date: '%k'}}", " " + simpleDateFormat("H").format(date)},
                {"{{" + seconds + " | date: '%l'}}", " " + simpleDateFormat("h").format(date)},
                {"{{" + seconds + " | date: '%m'}}", simpleDateFormat("MM").format(date)},
                {"{{" + seconds + " | date: '%M'}}", simpleDateFormat("mm").format(date)},
                {"{{" + seconds + " | date: '%p'}}", simpleDateFormat("a").format(date)},
                {"{{" + seconds + " | date: '%S'}}", simpleDateFormat("ss").format(date)},
                {"{{" + seconds + " | date: '%U'}}", "00"},
                {"{{" + seconds + " | date: '%W'}}", "00"},
                // 	Weekday as a decimal number, where 0 is Sunday and 6 is Saturday.
                // should be 6
                {"{{" + seconds + " | date: '%w'}}", "6"},
                {"{{" + seconds + " | date: '%x'}}", simpleDateFormat("MM/dd/yy").format(date)},
                {"{{" + seconds + " | date: '%X'}}", simpleDateFormat("HH:mm:ss").format(date)},
                {"{{" + seconds + " | date: 'x=%y'}}", "x=" + simpleDateFormat("yy").format(date)},
                {"{{" + seconds + " | date: '%Y'}}", simpleDateFormat("yyyy").format(date)},
                {"{{" + seconds + " | date: '%Z'}}", simpleDateFormat("zzzz").format(date)}
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat("render('" + test[0] + "') = ", rendered, is(test[1]));
        }
    }

    /*
     * def test_date
     *   assert_equal 'May', @filters.date(Time.parse("2006-05-05 10:00:00"), "%B")
     *   assert_equal 'June', @filters.date(Time.parse("2006-06-05 10:00:00"), "%B")
     *   assert_equal 'July', @filters.date(Time.parse("2006-07-05 10:00:00"), "%B")
     *
     *   assert_equal 'May', @filters.date("2006-05-05 10:00:00", "%B")
     *   assert_equal 'June', @filters.date("2006-06-05 10:00:00", "%B")
     *   assert_equal 'July', @filters.date("2006-07-05 10:00:00", "%B")
     *
     *   assert_equal '2006-07-05 10:00:00', @filters.date("2006-07-05 10:00:00", "")
     *   assert_equal '2006-07-05 10:00:00', @filters.date("2006-07-05 10:00:00", nil)
     *
     *   assert_equal '07/05/2006', @filters.date("2006-07-05 10:00:00", "%m/%d/%Y")
     *
     *   assert_equal "07/16/2004", @filters.date("Fri Jul 16 01:00:00 2004", "%m/%d/%Y")
     *
     *   assert_equal nil, @filters.date(nil, "%B")
     *
     *   assert_equal "07/05/2006", @filters.date(1152098955, "%m/%d/%Y")
     *   assert_equal "07/05/2006", @filters.date("1152098955", "%m/%d/%Y")
     * end
     */
    @Test
    public void applyOriginalTest() throws Exception {

        TemplateContext context = new TemplateContext();
        final Filter filter = Filter.getFilter("date");

        assertThat(filter.apply("Fri Jul 16 01:00:00 2004", context,"%m/%d/%Y"), is((Object)"07/16/2004"));

        assertThat(filter.apply(seconds("2006-05-05 10:00:00"), context, "%B"), is((Object)"May"));
        assertThat(filter.apply(seconds("2006-06-05 10:00:00"), context,"%B"), is((Object)"June"));
        assertThat(filter.apply(seconds("2006-07-05 10:00:00"), context,"%B"), is((Object)"July"));

        assertThat(filter.apply("2006-05-05 10:00:00", context,"%B"), is((Object)"May"));
        assertThat(filter.apply("2006-06-05 10:00:00", context,"%B"), is((Object)"June"));
        assertThat(filter.apply("2006-07-05 10:00:00", context,"%B"), is((Object)"July"));

        assertThat(filter.apply("2006-07-05 10:00:00", context,""), is((Object)"2006-07-05 10:00:00"));
        assertThat(filter.apply("2006-07-05 10:00:00", context,null), is((Object)"2006-07-05 10:00:00"));

        assertThat(filter.apply("2006-07-05 10:00:00", context,"%m/%d/%Y"), is((Object)"07/05/2006"));

        assertThat(filter.apply(null, context,"%B"), is((Object)null));

        assertThat(filter.apply(1152098955, context,"%m/%d/%Y"), is((Object)"07/05/2006"));
        assertThat(filter.apply("1152098955", context,"%m/%d/%Y"), is((Object)"07/05/2006"));
    }

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    private static long seconds(String str) throws Exception {
        long milliSeconds =  formatter.parse(str).getTime();
        return milliSeconds / 1000L;
    }

    public static SimpleDateFormat simpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern, Locale.ENGLISH);
    }

    @Test
    public void customDateTypeSupport() {
        // given
        class CustomDate {
            long time;

            public CustomDate(long time) {
                this.time = time;
            }
        }
        Filter f = Date.withCustomDateType(new CustomDateFormatSupport<CustomDate>() {

            @Override
            public ZonedDateTime getValue(CustomDate value) {
                return ZonedDateTime.ofInstant(Instant.ofEpochMilli(value.time), ZoneOffset.UTC);
            }

            @Override
            public boolean support(Object in) {
                return CustomDate.class.isInstance(in);
            }
        });
        Filter.registerFilter(f);

        // when
        CustomDate customDate = new CustomDate(1152098955000L);

        // then
        TemplateContext context = new TemplateContext();
        assertThat(Filter.getFilter("date").apply(customDate, context, "%m/%d/%Y"), is((Object) "07/05/2006"));
    }

    @Test
    public void testParseWithZoneInfo() {
        // given
        String val = "2010-10-31 00:00:00 -0500";
        // String val = "2021-01-27 00:00:00 EST";

        // when
        Object res = Filter.getFilter("date").apply(val, new TemplateContext(), "%Y-%m-%d %H:%M:%S %z");

        // then
        assertThat((String) res, is("2010-10-31 00:00:00 -0500"));
    }
    
    
    @Test
    public void test171() {
        java.util.Map<String, Object> values = new HashMap<>();
        values.put("date_with_t", "2020-01-01T12:30:00");
        values.put("date_with_space", "2020-01-01 12:30:00");

        Template t = Template.parse("Space: {{ date_with_space | date: '%Y' }} | T: {{ date_with_t | date: '%Y' }}");

        String result = t.render(values);
        assertEquals("Space: 2020 | T: 2020", result);
    }
}
