package liqp;

import liqp.parser.Inspectable;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TemplateTest {

    public static class ComparableBase implements Comparable<ComparableBase> {
        public final int val;

        public ComparableBase(int val) {
            this.val = val;
        }

        @Override
        public int compareTo(ComparableBase o) {
            return Integer.compare(val, o.val);
        }
    }

    static class Foo implements Inspectable {

        public String a = "A";
        private String b = "B";
        private String c = "C";

        public String getB() {
            return b;
        }
    }

    @Test
    public void renderObjectTest() throws RecognitionException {

        // `a` is public
        assertThat(Template.parse("{{foo.a}}").render(true, "foo", new Foo()), is("A"));

        // there is a public `getB()` method that exposes `b`
        assertThat(Template.parse("{{foo.b}}").render(true, "foo", new Foo()), is("B"));

        // `c` is not accessible
        assertThat(Template.parse("{{foo.c}}").render(true, "foo", new Foo()), is(""));
    }

    @Test
    public void renderJSONStringTest() throws RecognitionException {

        final String expected = "Hey";

        String rendered = Template.parse("{{mu}}").render("{\"mu\" : \"" + expected + "\"}");
        assertThat(rendered, is(expected));
    }

    @Test(expected = RuntimeException.class)
    public void renderJSONStringTestInvalidJSON() throws RecognitionException {
        Template.parse("mu").render("{\"key : \"value\"}"); // missing quote after `key`
    }

    @Test
    public void renderVarArgsTest() throws RecognitionException {

        final String expected = "Hey";

        String rendered = Template.parse("{{mu}}").render("mu", expected);
        assertThat(rendered, is(expected));

        rendered = Template.parse("{{a}}{{b}}{{c}}").render(
                "a", expected,
                "b", expected,
                "c", null
        );
        assertThat(rendered, is(expected + expected));

        rendered = Template.parse("{{a}}{{b}}{{c}}").render(
                "a", expected,
                "b", expected,
                "c" /* no value */
        );
        assertThat(rendered, is(expected + expected));

        rendered = Template.parse("{{a}}{{b}}{{c}}").render(
                "a", "A",
                "b", "B",
                "c", "C"
        );
        assertThat(rendered, is("ABC"));
    }

    @Test(expected = RuntimeException.class)
    public void renderVarArgsTestInvalidKey2() throws RecognitionException {
        Template.parse("mu").render(null, 456);
    }

    @Test
    public void renderMapWithPojosExistedNotRender() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("foo", new Foo());
        data.put("bar", "zoo");
        data.put("bear", true);

        String fooA = Template.parse("{{foo.a}}{{bar}}{{bear}}").render(data);

        assertThat(fooA, is("Azootrue"));
    }

    @Test
    public void parseWithInputStream() throws Exception {
        InputStream inputStream = new FileInputStream(new File("./snippets/header.html"));
        Template template = Template.parse(inputStream);
        assertThat(template.render(), is("HEADER\n"));
    }

    @Test
    public void testRenderInspectable() {
        // given
        Template template = Template.parse("{{ some.val }}");
        class MyInspectable implements Inspectable {
            public final Map<String, String> some = new HashMap<>();
            {
                some.put("val", "321");
            }
        }
        MyInspectable data = new MyInspectable();

        // when
        String res = template.render(data);

        // then
        assertEquals("321", res);
    }

    static class SampleDateInspectable implements Inspectable {
        public Date val;
        public SampleDateInspectable(Date date) {
            val = date;
        }
    }

    @Test
    public void testRenderInspectableDateType() {
        // given
        Template template = Template.parse("{{ val | date: '%e %b, %Y' }}");

        // legacy API: year should be 1900 + year, month is 0-based
        SampleDateInspectable sample = new SampleDateInspectable(new Date(120, Calendar.DECEMBER, 31));

        // when
        String res = template.render(sample);

        // then
        assertEquals("31 Dec, 2020", res);
    }

    @Test
    public void testRenderDateType() {
        // given
        Template template = Template.parse("{{ val | date: '%e %b, %Y' }}");

        Map<String, Object> sample = new HashMap<>();
        // legacy API: year should be 1900 + year, month is 0-based
        sample.put("val", new Date(120, Calendar.DECEMBER, 31));

        // when
        String res = template.render(sample);

        // then
        assertEquals("31 Dec, 2020", res);
    }

    @Test
    public void testDeepData() {
        // given
        Map<String, Object> data = getDeepData();
        Template template = Template.parse("{{a.b[2].d[3].e}}").withRenderSettings(new RenderSettings.Builder().withStrictVariables(true).withRaiseExceptionsInStrictMode(true).build());

        // when
        String rendered = template.render(data);

        // then
        assertEquals("ok", rendered);
    }

    @Test
    public void testDeepInspectable() {

        // given
        Inspectable data = new Inspectable() {
            public Inspectable a = new Inspectable() {
                public Object[] b = new Object[]{null, null, new Inspectable() {
                    public List d = new ArrayList();
                    {
                        d.add(new Object()); // 0
                        d.add(new Object()); // 1
                        d.add(new Object()); // 2
                        d.add(new HashMap() {{
                            put("e", ZonedDateTime.of(LocalDateTime.of(2021, 11, 3, 16, 40, 44), ZoneId.of("America/Los_Angeles")));
                        }
                        }); // 3
                    }
                }};
            };
        };
        Template template = Template.parse("{{ a.b[2].d[3].e | date: '%Y-%m-%d %H:%M:%S %Z' }}").withRenderSettings(new RenderSettings.Builder().withStrictVariables(true).withRaiseExceptionsInStrictMode(true).build());

        // when
        String rendered = template.render(data);

        // then
        assertEquals("2021-11-03 16:40:44 Pacific Daylight Time", rendered);
    }

    private Map<String, Object> getDeepData() {
        Map<String, Object> data = new HashMap<>();
        Map secondIndex = Collections.singletonMap("e", "ok");
        Object[] d = new Object[]{null, null, null, secondIndex};
        List<Object> firstIndex = new ArrayList<>();
        firstIndex.add(new Object()); // 0
        firstIndex.add(new Object()); // 1
        firstIndex.add(Collections.singletonMap("d", d)); // 2
        Map<String, Object> a = Collections.singletonMap("b", firstIndex);
        data.put("a", a);
        return data;
    }

}
