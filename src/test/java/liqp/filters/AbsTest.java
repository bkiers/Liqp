package liqp.filters;

import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
                {"{{ '-17.42' | abs }}", "17.42"}
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
