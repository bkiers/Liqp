package liqp.filters;

import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RoundTest {

    /*
        def test_round
          assert_template_result "5", "{{ input | round }}", 'input' => 4.6
          assert_template_result "4", "{{ '4.3' | round }}"
          assert_template_result "4.56", "{{ input | round: 2 }}", 'input' => 4.5612
          assert_template_result "5", "{{ price | round }}", 'price' => NumberLikeThing.new(4.6)
          assert_template_result "4", "{{ price | round }}", 'price' => NumberLikeThing.new(4.3)
        end
    */
    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ nil | round }}", "0", "{}"},
                {"{{ 'MU' | round }}", "0", "{}"},
                {"{{ input | round }}", "5", "{ \"input\": 4.6 }"},
                {"{{ '4.3' | round }}", "4", "{}"},
                {"{{ '10.5' | round }}", "11", "{}"},
                {"{{ input | round: 2 }}", "4.56", "{ \"input\": 4.5612 }"},
                {"{{ input | round: 2.999 }}", "4.56", "{ \"input\": 4.5612 }"},
                {"{{ price | round }}", "5", "{ \"price\": 4.6 }"},
                {"{{ price | round }}", "4", "{ \"price\": 4.3 }"}
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(test[2]));

            assertTrue(rendered.equals(test[1]) || rendered.equals(test[1].replace('.', ',')));
        }
    }
}
