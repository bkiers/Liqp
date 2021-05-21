package liqp.filters;

import liqp.RenderSettings;
import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class AppendTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ 'a' | append: 'b' }}", "ab"},
                {"{{ '' | append: '' }}", ""},
                {"{{ 1 | append: 23 }}", "123"},
                {"{{ nil | append: 'a' }}", "a"},
                {"{{ nil | append: nil }}", ""},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    /*
     * def test_append
     *   assigns = {'a' => 'bc', 'b' => 'd' }
     *   assert_template_result('bcd',"{{ a | append: 'd'}}",assigns)
     *   assert_template_result('bcd',"{{ a | append: b}}",assigns)
     * end
     */
    @Test
    public void applyOriginalTest() {

        final String assigns = "{\"a\":\"bc\", \"b\":\"d\" }";

        assertThat(Template.parse("{{ a | append: 'd'}}").render(assigns), is("bcd"));
        assertThat(Template.parse("{{ a | append: b}}").render(assigns), is("bcd"));
    }


    // 2007-11-01 15:25:00 +0900
    private final ZonedDateTime t = ZonedDateTime.of(
            LocalDateTime.of(2007, 11, 1, 15, 25, 0)
            , ZoneId.of("+09:00"));

    @Test
    public void testAppendToDateType() {

        // after time
        Map<String, Object> data = Collections.singletonMap("a", t);
        String res = Template.parse("{{ a | append: '!' }}").render(data);
        assertEquals("2007-11-01 15:25:00 +0900!", res);

        // before time
        res = Template.parse("{{ '!' | append: a }}").render(data);
        assertEquals("!2007-11-01 15:25:00 +0900", res);

    }

    @Test
    public void testAppendToDateTypeEager() {
        RenderSettings eager = new RenderSettings.Builder().withEvaluateMode(RenderSettings.EvaluateMode.EAGER).build();
        Map<String, Object> data = Collections.singletonMap("a", t);

        String res = Template.parse("{{ '!' | append: a }}").withRenderSettings(eager).render(data);
        assertEquals("!2007-11-01 15:25:00 +0900", res);

        res = Template.parse("{{ a | append: '!' }}").withRenderSettings(eager).render(data);
        assertEquals("2007-11-01 15:25:00 +0900!", res);
    }
}
