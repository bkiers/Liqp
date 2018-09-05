package liqp.filters;

import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CeilTest {

    /*
        def test_ceil
          assert_template_result "5", "{{ input | ceil }}", 'input' => 4.6
          assert_template_result "5", "{{ '4.3' | ceil }}"
          assert_template_result "5", "{{ price | ceil }}", 'price' => NumberLikeThing.new(4.6)
        end
    */
    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ input | ceil }}", "5", "{ \"input\": 4.6 }"},
                {"{{ '4.3' | ceil }}", "5", "{}"},
                {"{{ price | ceil }}", "5", "{ \"price\": 4.6 }"}
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(test[2]));

            assertThat(rendered, is(test[1]));
        }
    }
}
