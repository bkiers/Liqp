package liqp.filters;

import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class At_LeastTest {

    /*
        def test_at_least
          assert_template_result "5", "{{ 5 | at_least:4 }}"
          assert_template_result "5", "{{ 5 | at_least:5 }}"
          assert_template_result "6", "{{ 5 | at_least:6 }}"

          assert_template_result "5", "{{ 4.5 | at_least:5 }}"
          assert_template_result "6", "{{ width | at_least:5 }}", 'width' => NumberLikeThing.new(6)
          assert_template_result "5", "{{ width | at_least:5 }}", 'width' => NumberLikeThing.new(4)
          assert_template_result "6", "{{ 5 | at_least: width }}", 'width' => NumberLikeThing.new(6)
        end
    */
    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ 5 | at_least:4 }}", "5", "{}"},
                {"{{ 5 | at_least:5 }}", "5", "{}"},
                {"{{ 5 | at_least:6 }}", "6", "{}"},

                {"{{ 4.5 | at_least:5 }}", "5", "{}"},
                {"{{ width | at_least:5 }}", "6", "{ \"width\": 6 }"},
                {"{{ width | at_least:5 }}", "5", "{ \"width\": 4 }"},
                {"{{ 5 | at_least: width }}", "6", "{ \"width\": 6 }"}
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(test[2]));

            assertThat(rendered, is(test[1]));
        }
    }
}
