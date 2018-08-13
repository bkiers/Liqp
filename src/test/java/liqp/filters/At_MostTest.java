package liqp.filters;

import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class At_MostTest {

    /*
        def test_at_most
          assert_template_result "4", "{{ 5 | at_most:4 }}"
          assert_template_result "5", "{{ 5 | at_most:5 }}"
          assert_template_result "5", "{{ 5 | at_most:6 }}"

          assert_template_result "4.5", "{{ 4.5 | at_most:5 }}"
          assert_template_result "5", "{{ width | at_most:5 }}", 'width' => NumberLikeThing.new(6)
          assert_template_result "4", "{{ width | at_most:5 }}", 'width' => NumberLikeThing.new(4)
          assert_template_result "4", "{{ 5 | at_most: width }}", 'width' => NumberLikeThing.new(4)
        end
    */
    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ 5 | at_most:4 }}", "4", "{}"},
                {"{{ 5 | at_most:5 }}", "5", "{}"},
                {"{{ 5 | at_most:6 }}", "5", "{}"},

                {"{{ 4.5 | at_most:5 }}", "4.5", "{}"},
                {"{{ width | at_most:5 }}", "5", "{ \"width\": 6 }"},
                {"{{ width | at_most:5 }}", "4", "{ \"width\": 4 }"},
                {"{{ 5 | at_most: width }}", "4", "{ \"width\": 4 }"}
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(test[2]));

            assertThat(rendered, is(test[1]));
        }
    }
}
