package liqp.filters;

import liqp.Template;
import liqp.TemplateParser;
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

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    @Test
    public void ensureDateTypeIsZero() {
        String res = TemplateParser.DEFAULT.parse("{{ a | abs }}").render(Collections.singletonMap("a", LocalDateTime.now()));
        assertEquals("0", res);

        TemplateParser parser = new TemplateParser.Builder()
                .withEvaluateMode(TemplateParser.EvaluateMode.EAGER)
                .build();
        
        res = parser.parse("{{ a | abs }}").render(Collections.singletonMap("a", LocalDateTime.now()));
        assertEquals("0", res);
    }

    @Test
    public void testMathPrecise() {

    }
}
