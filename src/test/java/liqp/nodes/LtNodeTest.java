package liqp.nodes;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateParser;
import liqp.TemplateTest;
import liqp.parser.Inspectable;

public class LtNodeTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{% if nil < 42.09 %}yes{% else %}no{% endif %}", "no"},
                {"{% if 42.1 < false %}yes{% else %}no{% endif %}", "no"},
                {"{% if 42.1 < true %}yes{% else %}no{% endif %}", "no"},
                {"{% if a < 42.09 %}yes{% else %}no{% endif %}", "no"},
                {"{% if 42.1 < 42.09 %}yes{% else %}no{% endif %}", "no"},
                {"{% if 42.1 < 42.1000001 %}yes{% else %}no{% endif %}", "yes"},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
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

        String value = TemplateParser.DEFAULT.parse("{% if a < a %}yes{% else %}no{% endif %}").render(data);
        assertEquals("no", value);
        value = TemplateParser.DEFAULT.parse("{% if a < b %}yes{% else %}no{% endif %}").render(data);
        assertEquals("no", value);
        value = TemplateParser.DEFAULT.parse("{% if a < c %}yes{% else %}no{% endif %}").render(data);
        assertEquals("yes", value);
        value = TemplateParser.DEFAULT.parse("{% if a < d %}yes{% else %}no{% endif %}").render(data);
        assertEquals("no", value);
    }
    @Test
    public void testComparableTypes() {

        Map<String, Object> data = new HashMap<>();
        data.put("a", new TemplateTest.ComparableBase(10));
        data.put("b", new TemplateTest.ComparableBase(9));
        data.put("c", new TemplateTest.ComparableBase(11));

        String value = TemplateParser.DEFAULT.parse("{% if a < a %}yes{% else %}no{% endif %}").render(data);
        assertEquals("no", value);
        value = TemplateParser.DEFAULT.parse("{% if a < b %}yes{% else %}no{% endif %}").render(data);
        assertEquals("no", value);
        value = TemplateParser.DEFAULT.parse("{% if a < c %}yes{% else %}no{% endif %}").render(data);
        assertEquals("yes", value);
    }
}
