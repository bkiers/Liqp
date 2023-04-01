package liqp;

import liqp.filters.Filter;
import liqp.blocks.Block;
import liqp.tags.Tag;
import liqp.nodes.LNode;
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
import java.util.Locale;
import java.util.Map;

import static liqp.TestUtils.assertPatternResultEquals;
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
        assertThat(TemplateParser.DEFAULT.parse("{{foo.a}}").render(true, "foo", new Foo()), is("A"));

        // there is a public `getB()` method that exposes `b`
        assertThat(TemplateParser.DEFAULT.parse("{{foo.b}}").render(true, "foo", new Foo()), is("B"));

        // `c` is not accessible
        assertThat(TemplateParser.DEFAULT.parse("{{foo.c}}").render(true, "foo", new Foo()), is(""));
    }

    @Test
    public void renderJSONStringTest() throws RecognitionException {

        final String expected = "Hey";

        String rendered = TemplateParser.DEFAULT.parse("{{mu}}").render("{\"mu\" : \"" + expected + "\"}");
        assertThat(rendered, is(expected));
    }

    @Test(expected = RuntimeException.class)
    public void renderJSONStringTestInvalidJSON() throws RecognitionException {
        TemplateParser.DEFAULT.parse("mu").render("{\"key : \"value\"}"); // missing quote after `key`
    }

    @Test
    public void renderVarArgsTest() throws RecognitionException {

        final String expected = "Hey";

        String rendered = TemplateParser.DEFAULT.parse("{{mu}}").render("mu", expected);
        assertThat(rendered, is(expected));

        rendered = TemplateParser.DEFAULT.parse("{{a}}{{b}}{{c}}").render(
                "a", expected,
                "b", expected,
                "c", null
        );
        assertThat(rendered, is(expected + expected));

        rendered = TemplateParser.DEFAULT.parse("{{a}}{{b}}{{c}}").render(
                "a", expected,
                "b", expected,
                "c" /* no value */
        );
        assertThat(rendered, is(expected + expected));

        rendered = TemplateParser.DEFAULT.parse("{{a}}{{b}}{{c}}").render(
                "a", "A",
                "b", "B",
                "c", "C"
        );
        assertThat(rendered, is("ABC"));
    }

    @Test(expected = RuntimeException.class)
    public void renderVarArgsTestInvalidKey2() throws RecognitionException {
        TemplateParser.DEFAULT.parse("mu").render(null, 456);
    }

    @Test
    public void renderMapWithPojosExistedNotRender() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("foo", new Foo());
        data.put("bar", "zoo");
        data.put("bear", true);

        String fooA = TemplateParser.DEFAULT.parse("{{foo.a}}{{bar}}{{bear}}").render(data);

        assertThat(fooA, is("Azootrue"));
    }

    @Test
    public void parseWithInputStream() throws Exception {
        InputStream inputStream = new FileInputStream(new File("./snippets/header.html"));
        Template template = TemplateParser.DEFAULT.parse(inputStream);
        assertThat(template.render(), is("HEADER\n"));
    }

    @Test
    public void testRenderInspectable() {
        // given
        Template template = TemplateParser.DEFAULT.parse("{{ some.val }}");
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
        Template template = TemplateParser.DEFAULT.parse("{{ val | date: '%e %b, %Y' }}");

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
        Template template = TemplateParser.DEFAULT.parse("{{ val | date: '%e %b, %Y' }}");

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
        
        TemplateParser parser = new TemplateParser.Builder().withRenderSettings(
                new RenderSettings.Builder().withStrictVariables(true)
                        .build())
                .withErrorMode(TemplateParser.ErrorMode.strict)
                .build();
        
        Template template = parser.parse("{{a.b[2].d[3].e}}");

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
        
        TemplateParser parser = new TemplateParser.Builder().withRenderSettings(
                new RenderSettings.Builder().withStrictVariables(true)
                        .build())
                .withErrorMode(TemplateParser.ErrorMode.strict)
                .build();
        
        Template template = parser.parse("{{ a.b[2].d[3].e | date: '%Y-%m-%d %H:%M:%S %Z' }}");

        // when
        String rendered = template.render(data);

        // then
        assertEquals("2021-11-03 16:40:44 Pacific Daylight Time", rendered);
    }

    @Test
    public void testCustomTagMissingErrorReporting() {
        try {
            TemplateParser.DEFAULT.parse("{% custom_tag %}");
        } catch (Exception e) {
            assertEquals("parser error \"Invalid Tag: 'custom_tag'\" on line 1, index 3", e.getMessage());
        }
    }
    
    @Test
    public void testWithCustomTag() {
        // given
        TemplateParser parser = new TemplateParser.Builder().withParseSettings(new ParseSettings.Builder()
                .with(new Tag("custom_tag") {
                    @Override
                    public Object render(TemplateContext context, LNode... nodes) {
                        return "xxx";
                    }
                })
                .build()).build();
        
        Template template = parser.parse("{% custom_tag %}");
        
        // then
        assertEquals("xxx", template.render());
    }

    @Test
    public void testWithCustomBlock() {
        // given
        TemplateParser parser = new TemplateParser.Builder().withParseSettings(
                new ParseSettings.Builder().with(new Block("custom_uppercase_block") {
                    @Override
                    public Object render(TemplateContext context, LNode... nodes) {
                        LNode block = nodes[0];
                        Object res = block.render(context);
                        if (res != null) {
                            return res.toString().toUpperCase(Locale.US);
                        }
                        return null;
                    }
                }).build()).build();

        Template template = parser.parse(
                "{% custom_uppercase_block %} some text {% endcustom_uppercase_block %}");

        // then
        assertEquals(" SOME TEXT ", template.render());
    }

    @Test
    public void testWithCustomFilter() {
        // given
        TemplateParser parser = new TemplateParser.Builder().withParseSettings(new ParseSettings.Builder()
                .with(new Filter("sum"){
                    @Override
                    public Object apply(Object value, TemplateContext context, Object... params) {
                        Object[] numbers = super.asArray(value, context);
                        double sum = 0;
                        for(Object obj : numbers) {
                            sum += super.asNumber(obj).doubleValue();
                        }
                        return sum;
                    }
                })
                .build()).build();
        
        Template template = parser.parse("{{ numbers | sum }}");

        String rendered = template.render("{\"numbers\" : [1, 2, 3, 4, 5]}");

        // then
        assertEquals("15.0", rendered);
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

    @Test
    public void testUseVariableAndTimesFilter() throws RecognitionException {
        assertPatternResultEquals(TemplateParser.DEFAULT_LIQP, "true",
                "{% assign comparingValue = 98 | times: 1.0 %}{{ 99 > comparingValue }}");
    }

    @Test
    public void testUseVariableAndDividedByFilter() throws RecognitionException {
        assertPatternResultEquals(TemplateParser.DEFAULT_LIQP, "true",
                "{% assign comparingValue = 98 | divided_by: 1.0 %}{{ 99 > comparingValue }}");
    }

}
