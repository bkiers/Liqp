package liqp.nodes;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateParser;
import liqp.TemplateTest;
import liqp.exceptions.LiquidException;
import liqp.parser.Flavor;
import liqp.parser.Inspectable;

public class GtNodeTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{% if nil > 42.09 %}yes{% else %}no{% endif %}", "no"},
                {"{% if 42.1 > false %}yes{% else %}no{% endif %}", "no"},
                {"{% if 42.1 > true %}yes{% else %}no{% endif %}", "no"},
                {"{% if a > 42.09 %}yes{% else %}no{% endif %}", "no"},
                {"{% if 42.1 > 42.09 %}yes{% else %}no{% endif %}", "yes"},
                {"{% if 42.1 > 42.1000001 %}yes{% else %}no{% endif %}", "no"},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT_JEKYLL.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    @Test
    public void testDateTypes() {
        // so many ZoneOffset.systemDefault(), because
        // broken java.util.Date cannot another way
        @SuppressWarnings("unused")
        Inspectable data = new Inspectable() {
            public Date a = new Date(100 /* milliseconds */);
            public LocalDateTime b = LocalDateTime.ofInstant(Instant.ofEpochMilli(99), ZoneOffset.systemDefault());
            public ZonedDateTime c = ZonedDateTime.of(LocalDateTime.ofInstant(Instant.ofEpochMilli(101), ZoneOffset.systemDefault()), ZoneOffset.systemDefault());
            public ZonedDateTime d = ZonedDateTime.of(LocalDateTime.ofInstant(Instant.ofEpochMilli(100), ZoneOffset.systemDefault()), ZoneOffset.systemDefault());
        };

        String value = TemplateParser.DEFAULT.parse("{% if a > a %}yes{% else %}no{% endif %}").render(data);
        assertEquals("no", value);
        value = TemplateParser.DEFAULT.parse("{% if a > b %}yes{% else %}no{% endif %}").render(data);
        assertEquals("yes", value);
        value = TemplateParser.DEFAULT.parse("{% if a > c %}yes{% else %}no{% endif %}").render(data);
        assertEquals("no", value);
        value = TemplateParser.DEFAULT.parse("{% if a > d %}yes{% else %}no{% endif %}").render(data);
        assertEquals("no", value);
    }

    @Test
    public void testComparableTypes() {

        Map<String, Object> data = new HashMap<>();
        data.put("a", new TemplateTest.ComparableBase(10));
        data.put("b", new TemplateTest.ComparableBase(9));
        data.put("c", new TemplateTest.ComparableBase(11));

        String value = TemplateParser.DEFAULT.parse("{% if a > a %}yes{% else %}no{% endif %}").render(data);
        assertEquals("no", value);
        value = TemplateParser.DEFAULT.parse("{% if a >= b %}yes{% else %}no{% endif %}").render(data);
        assertEquals("yes", value);
        value = TemplateParser.DEFAULT.parse("{% if a >= c %}yes{% else %}no{% endif %}").render(data);
        assertEquals("no", value);
    }

    @Test
    public void testBug267ExpressionInOutputAsLiquid() {
        try {
            new TemplateParser.Builder().withFlavor(Flavor.LIQUID).build().parse("{{ 98 > 97 }}").render();
            fail();
        } catch (LiquidException e) {
            assertTrue(e.getMessage().contains("parser error"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testBug267ExpressionInOutputAsJekyll() {
        Template.ContextHolder contextHolder = new Template.ContextHolder();
        String res = new TemplateParser.Builder()
                .withFlavor(Flavor.JEKYLL)
                .build()
                .parse("{{ 98 > 97 }}")
                .withContextHolder(contextHolder)
                .render();
        assertEquals("98", res);
        List<Exception> errors = contextHolder.getContext().errors();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).getMessage().contains("unexpected output"));
    }

    @Test
    public void testBug267StringVsNumber() {
        try {
            new TemplateParser.Builder()
                    .withFlavor(Flavor.JEKYLL)
                    .build()
                    .parse("{% if 98 > '98' %}true{% else %}false{% endif %}")
                    .render();
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("not the same type"));
        }
    }

    @Test
    public void testFilterCompare() {
        String result = new TemplateParser.Builder().withFlavor(Flavor.JEKYLL).build() //
            .parse("{% assign score = 0 | plus: 1.0 %}{% if score > 0 %}true{% else %}false{% endif %}")
            .render();
        assertTrue(Boolean.parseBoolean(result));
    }

    @Test
    public void testStrictModeDisabled() {
        String[][] tests = {
                {"{% if 0 > 'A' %}yes{% else %}no{% endif %}", "no"},
                {"{% if 'A' > 0 %}yes{% else %}no{% endif %}", "no"},
                {"{% if false > 1 %}yes{% else %}no{% endif %}", "no"},
        };
        TemplateParser templateParser = new TemplateParser.Builder().withStrictTypedExpressions(false).build();
        for (String[] test : tests) {
            String rendered = templateParser.parse(test[0]).render();
            assertThat(rendered, is(test[1]));
        }
    }
}
