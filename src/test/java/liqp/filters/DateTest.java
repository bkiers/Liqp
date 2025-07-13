package liqp.filters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;
import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;
import liqp.filters.date.CustomDateFormatSupport;
import liqp.parser.Flavor;
import liqp.spi.SPIHelper;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Test;
import ua.co.k.strftime.formatters.HybridFormat;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DateTest {

    private TemplateParser dateFilterSetting;

    @Before
    public void init() {
        // reset
        dateFilterSetting = new TemplateParser.Builder().withFilter(new Date()).build();
        SPIHelper.applyCustomDateTypes();
    }

    // NOTE: you have to put your machine in US/Eastern time for this test to pass
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
                {"{{" + seconds + " | date: '%k'}}", withOptionalPadding(2, ' ', simpleDateFormat("H").format(date))},
                {"{{" + seconds + " | date: '%l'}}", withOptionalPadding(2, ' ', simpleDateFormat("h").format(date))},
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

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat("render('" + test[0] + "') = ", rendered, is(test[1]));
        }
    }

    private String withOptionalPadding(int count, char padSymbol, String input) {
        return HybridFormat.padLeft(input, count, padSymbol);
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
        final Filter filter = dateFilterSetting.filters.get("date");

        assertThat(filter.apply("Fri Jul 16 01:00:00 2004", context, "%m/%d/%Y"), is((Object)"07/16/2004"));
        assertThat(filter.apply("Fri Jul 9 01:00:00 2004", context, "%m/%d/%Y"), is((Object)"07/09/2004"));

        assertThat(filter.apply("Fri Jul 16 01:00 2004", context, "%m/%d/%Y"), is((Object)"07/16/2004"));

        assertThat(filter.apply(seconds("2006-05-05 10:00:00"), context, "%B"), is((Object)"May"));
        assertThat(filter.apply(seconds("2006-06-05 10:00:00"), context, "%B"), is((Object)"June"));
        assertThat(filter.apply(seconds("2006-07-05 10:00:00"), context, "%B"), is((Object)"July"));

        assertThat(filter.apply("2006-05-05 10:00:00", context, "%B"), is((Object)"May"));
        assertThat(filter.apply("2006-06-05 10:00:00", context, "%B"), is((Object)"June"));
        assertThat(filter.apply("2006-07-05 10:00:00", context, "%B"), is((Object)"July"));

        assertThat(filter.apply("2006-07-05 10:00:00", context, ""), is((Object)"2006-07-05 10:00:00"));
        assertThat(filter.apply("2006-07-05 10:00:00", context), is((Object)"2006-07-05 10:00:00"));
        assertThat(filter.apply("2006-07-05 10:00:00", context, (Object)null), is((Object)"2006-07-05 10:00:00"));
        assertThat(filter.apply("2006-07-05 10:00:00", context, (Object[])null), is((Object)"2006-07-05 10:00:00"));
        assertThat(filter.apply("2006-07-05 10:00:00", context, new Object[0]), is((Object)"2006-07-05 10:00:00"));

        assertThat(filter.apply("2006-07-05 10:00:00", context, "%m/%d/%Y"), is((Object)"07/05/2006"));

        assertThat(filter.apply(null, context, "%B"), is((Object)null));

        assertThat(filter.apply(1152098955, context, "%m/%d/%Y"), is((Object)"07/05/2006"));
        assertThat(filter.apply("1152098955", context, "%m/%d/%Y"), is((Object)"07/05/2006"));
        TemplateParser parser = new TemplateParser.Builder().withFlavor(Flavor.LIQUID).withDefaultTimeZone(ZoneOffset.UTC).build();
        TemplateContext anotherZone = new TemplateContext(parser, new LinkedHashMap<>());
        assertThat(filter.apply("1152098955", anotherZone, "%H"), is((Object)"11"));
        assertThat(filter.apply(1152098955, anotherZone, "%H"), is((Object)"11"));
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

        TemplateParser parser = new TemplateParser.Builder().withFilter(f).build();

        // when
        CustomDate customDate = new CustomDate(1152098955000L);

        // then
        TemplateContext context = new TemplateContext();
        assertThat(parser.filters.get("date").apply(customDate, context, "%m/%d/%Y"), is(
                (Object) "07/05/2006"));
    }

    @Test
    public void testParseWithZoneInfo() {
        // given
        String val = "2010-10-31 00:00:00 -0500";
        // String val = "2021-01-27 00:00:00 EST";

        // when
        Object res = dateFilterSetting.filters.get("date").apply(val, new TemplateContext(),
                "%Y-%m-%d %H:%M:%S %z");

        // then
        assertThat((String) res, is("2010-10-31 00:00:00 -0500"));
    }
    
    
    @Test
    public void test171() {
        java.util.Map<String, Object> values = new HashMap<>();
        values.put("date_with_t", "2020-01-01T12:30:00");
        values.put("date_with_space", "2020-01-01 12:30:00");

        Template t = TemplateParser.DEFAULT.parse("Space: {{ date_with_space | date: '%Y' }} | T: {{ date_with_t | date: '%Y' }}");

        String result = t.render(values);
        assertEquals("Space: 2020 | T: 2020", result);
    }

    @Test
    public void test240() {
        assertEquals("10-13", TemplateParser.DEFAULT.parse("{{ \"2022-10-13 12:06:04\" | date: \"%m-%e\" }}").render());
        assertEquals("10-13", TemplateParser.DEFAULT.parse("{{ \"2022-10-13\" | date: \"%m-%e\" }}").render());
        assertEquals("10-13", TemplateParser.DEFAULT.parse("{{ \"13-10-2022\" | date: \"%m-%e\" }}").render());
    }

    @Test
    public void test298InstantWhenEpochBeginAtUTC() {
        Instant instant = Instant.ofEpochSecond(0);
        TemplateParser parser = new TemplateParser.Builder().withDefaultTimeZone(ZoneOffset.UTC).build();
        String res = parser.parse("{{ val }}").render("val", instant);
        assertEquals("1970-01-01 00:00:00 Z", res);
    }

    // https://github.com/bkiers/Liqp/issues/309
    @Test
    public void testSupportedDateStrings() {
        String[] tests = {
                "now",
                "today",
                "MARCH",
                "2024 MAR",
                "2 mar",
                "2 MAR",
                "march 2nd",
                "MARCH 2",
                "MARCH 2nd",
                "MARCH 3RD",
                "MARCH 3rD",
                "MARCH 4th",
                "MARCH 5th",
                "MARCH 10th",
                "2010-10-31",
                "Aug 2000",
                "Aug 31",
                "Wed Nov 28 14:33:20 2001",
                "Wed, 05 Oct 2011 22:26:12 -0400",
                "Wed, 05 Oct 2011 02:26:12 GMT",
                "Nov 29 14:33:20 2001",
                "05 Oct 2011 22:26:12 -0400",
                "06 Oct 2011 02:26:12 GMT",
                "2011-10-05T22:26:12-04:00",
                "0:00",
                "1:00",
                "01:00",
                "12:00",
                "16:30",
                "3/2024",
                "01/03",
                "03/31",
                "2001/03",
                "01/2003",
                "70-10-31"
        };

        for (String test : tests) {
            // Parse every test date. If this fails, the rendering engine will just return the
            // string. The '%s' will convert the date into a timestamp we check that what is
            // returned is a sequence of digits.
            String rendered = TemplateParser.DEFAULT.parse("{% assign d = '" + test + "' | date: '%s' %}{{ d }}").render();
            assertTrue("Failed to parse: '" + test + "'", rendered.matches("\\d+"));
        }
    }

    @Test
    public void testSupportedDateStringsValues() {
        Object[][] tests = {
                { "1 March", "%d %m %Y", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(3).withDayOfMonth(1), "dd MM y"},
                { "MAR", "%b", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(3), "MMM"},
                { "MARCH", "%B", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(3), "MMMM"},
                { "2024 MAR", "%Y %b", (Supplier<LocalDateTime>)() -> LocalDateTime.of(LocalDate.of(2024, 3, 1),
                        LocalTime.now()), "yyyy MMM"},
                { "2 mar", "%e %b", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(3).withDayOfMonth(2), "d MMM"},
                { "2 MAR", "%e %B", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(3).withDayOfMonth(2), "d MMMM"},
                { "march 2nd", "%B %e", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(3).withDayOfMonth(2), "MMMM d"},
                { "MARCH 2", "%B %e", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(3).withDayOfMonth(2), "MMMM d"},
                { "MARCH 2nd", "%B %e", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(3).withDayOfMonth(2), "MMMM d"},
                { "MARCH 3RD", "%B %e", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(3).withDayOfMonth(3), "MMMM d"},
                { "MARCH 3rD", "%B %e", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(3).withDayOfMonth(3), "MMMM d"},
                { "MARCH 4th", "%B %e", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(3).withDayOfMonth(4), "MMMM d"},
                { "MARCH 5th", "%B %e", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(3).withDayOfMonth(5), "MMMM d"},
                { "MARCH 10th", "%B %e", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(3).withDayOfMonth(10), "MMMM d"},
                { "2010-10-31", "%Y-%m-%d", (Supplier<LocalDateTime>)() -> LocalDateTime.of(LocalDate.of(2010, 10, 31), LocalTime.now()), "yyyy-MM-dd"},
                { "Aug 2000", "%b %Y", (Supplier<LocalDateTime>)() -> LocalDateTime.of(LocalDate.of(2000, 8, 1), LocalTime.now()), "MMM yyyy"},
                { "Aug 31", "%b %d", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(8).withDayOfMonth(31), "MMM dd"},
                { "Wed Nov 28 14:33:20 2001", "%a %b %d %H:%M:%S %Y", (Supplier<LocalDateTime>)() -> LocalDateTime.of(LocalDate.of(2001, 11, 28), LocalTime.parse("14:33:20")), "EEE MMM dd HH:mm:ss yyyy"},
                { "Wed, 05 Oct 2011 22:26:12 -0400", "%a, %d %b %Y %H:%M:%S", (Supplier<LocalDateTime>)() -> LocalDateTime.of(LocalDate.of(2011, 10, 5), LocalTime.parse("22:26:12")), "EEE, dd MMM yyyy HH:mm:ss"},
                { "Wed, 05 Oct 2011 02:26:12 GMT", "%a, %d %b %Y %H:%M:%S", (Supplier<LocalDateTime>)() -> LocalDateTime.of(LocalDate.of(2011, 10, 5), LocalTime.parse("02:26:12")), "EEE, dd MMM yyyy HH:mm:ss"},
                { "Nov 29 14:33:20 2001", "%b %d %H:%M:%S %Y", (Supplier<LocalDateTime>)() -> LocalDateTime.of(LocalDate.of(2001, 11, 29), LocalTime.parse("14:33:20")), "MMM dd HH:mm:ss yyyy"},
                { "05 Oct 2011 22:26:12 -0400", "%d %b %Y %H:%M:%S", (Supplier<LocalDateTime>)() -> LocalDateTime.of(LocalDate.of(2011, 10, 5), LocalTime.parse("22:26:12")), "dd MMM yyyy HH:mm:ss"},
                { "06 Oct 2011 02:26:12 GMT", "%d %b %Y %H:%M:%S", (Supplier<LocalDateTime>)() -> LocalDateTime.of(LocalDate.of(2011, 10, 6), LocalTime.parse("02:26:12")), "dd MMM yyyy HH:mm:ss"},
//                { "2011-10-05T22:26:12-04:00", "%Y-%m-%dT%H:%M:%S", (Supplier<LocalDateTime>)() -> LocalDateTime.of(LocalDate.of(2011, 10, 5), LocalTime.parse("22:26:12")), "yyyy-MM-dd'T'HH:mm:ss"},
                { "0:00", "%H:%M", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withHour(0).withMinute(0), "HH:mm"},
                { "1:00", "%H:%M", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withHour(1).withMinute(0), "HH:mm"},
                { "01:00", "%H:%M", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withHour(1).withMinute(0), "HH:mm"},
                { "12:00", "%H:%M", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withHour(12).withMinute(0), "HH:mm"},
                { "16:30", "%H:%M", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withHour(16).withMinute(30), "HH:mm"},
                { "3/2024", "%m/%Y", (Supplier<LocalDateTime>)() -> LocalDateTime.of(LocalDate.of(2024, 3, 1), LocalTime.now()), "MM/yyyy"},
                { "01/03", "%m/%d", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(1).withDayOfMonth(3), "MM/dd"},
                { "03/31", "%m/%d", (Supplier<LocalDateTime>)() -> LocalDateTime.now().withMonth(3).withDayOfMonth(31), "MM/dd"},
                { "2001/03", "%Y/%m", (Supplier<LocalDateTime>)() -> LocalDateTime.of(LocalDate.of(2001, 3, 1), LocalTime.now()), "yyyy/MM"},
                { "01/2003", "%m/%Y", (Supplier<LocalDateTime>)() -> LocalDateTime.of(LocalDate.of(2003, 1, 1), LocalTime.now()), "MM/yyyy"},
                { "70-10-31", "%y-%m-%d", (Supplier<LocalDateTime>)() -> LocalDateTime.of(LocalDate.of(1970, 10, 31), LocalTime.now()), "yy-MM-dd"}
        };
        for (Object[] test : tests) {
            String rendered;
            try {
                rendered = TemplateParser.DEFAULT.parse("{% assign d = '" + test[0] + "' | date: '" + test[1] + "' %}{{ d }}").render();
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse: '" + test[0] + "' with pattern '" + test[1] + "'", e);
            }
            LocalDateTime localDate = null;
            String pattern = null;
            try {
                localDate = ((Supplier<LocalDateTime>) test[2]).get();
                pattern = (String) test[3];
                String expected = localDate.format(
                        DateTimeFormatter.ofPattern(pattern));
                assertEquals(""+test[0], expected, rendered);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create java complementary: '" + localDate + "' with pattern '" + pattern + "'", e);
            }
        }
    }
}
