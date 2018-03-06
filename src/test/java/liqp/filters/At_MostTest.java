package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
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
                {"", ""},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
