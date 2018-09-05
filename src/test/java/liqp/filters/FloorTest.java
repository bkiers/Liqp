package liqp.filters;

import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FloorTest {

    /*
        def test_floor
          assert_template_result "4", "{{ input | floor }}", 'input' => 4.6
          assert_template_result "4", "{{ '4.3' | floor }}"
          assert_template_result "5", "{{ price | floor }}", 'price' => NumberLikeThing.new(5.4)
        end
    */
    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ input | floor }}", "4", "{ \"input\": 4.6 }"},
                {"{{ '4.3' | floor }}", "4", "{}"},
                {"{{ price | floor }}", "5", "{ \"price\": 5.4 }"}
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(test[2]));

            assertThat(rendered, is(test[1]));
        }
    }
}
