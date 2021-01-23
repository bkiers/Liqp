package liqp.filters;

import liqp.RenderSettings;
import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class AbsTest {

    /*
        def test_abs
          assert_template_result "17", "{{ 17 | abs }}"
          assert_template_result "17", "{{ -17 | abs }}"
          assert_template_result "17", "{{ '17' | abs }}"
          assert_template_result "17", "{{ '-17' | abs }}"
          assert_template_result "0", "{{ 0 | abs }}"
          assert_template_result "0", "{{ '0' | abs }}"
          assert_template_result "17.42", "{{ 17.42 | abs }}"
          assert_template_result "17.42", "{{ -17.42 | abs }}"
          assert_template_result "17.42", "{{ '17.42' | abs }}"
          assert_template_result "17.42", "{{ '-17.42' | abs }}"
        end
    */
    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ 17 | abs }}", "17"},
                {"{{ -17 | abs }}", "17"},
                {"{{ '17' | abs }}", "17"},
                {"{{ '-17' | abs }}", "17"},
                {"{{ 0 | abs }}", "0"},
                {"{{ '0' | abs }}", "0"},
                {"{{ 17.42 | abs }}", "17.42"},
                {"{{ -17.42 | abs }}", "17.42"},
                {"{{ '17.42' | abs }}", "17.42"},
                {"{{ '-17.42' | abs }}", "17.42"},
                {"{{  '0.2' | plus: '0.1' | abs }}", "0.3"}
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    @Test
    public void ensureDateTypeIsZero() {
        String res = Template.parse("{{ a | abs }}").render(Collections.singletonMap("a", LocalDateTime.now()));
        assertEquals("0", res);

        RenderSettings eager = new RenderSettings.Builder().withEvaluateMode(RenderSettings.EvaluateMode.EAGER).build();
        res = Template.parse("{{ a | abs }}").withRenderSettings(eager).render(Collections.singletonMap("a", LocalDateTime.now()));
        assertEquals("0", res);
    }

    @Test
    public void testMathPrecise() {

    }
}
