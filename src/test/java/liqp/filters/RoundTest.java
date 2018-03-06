package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RoundTest {

    /*
        def test_round
          assert_template_result "5", "{{ input | round }}", 'input' => 4.6
          assert_template_result "4", "{{ '4.3' | round }}"
          assert_template_result "4.56", "{{ input | round: 2 }}", 'input' => 4.5612
          assert_raises(Liquid::FloatDomainError) do
            assert_template_result "4", "{{ 1.0 | divided_by: 0.0 | round }}"
          end

          assert_template_result "5", "{{ price | round }}", 'price' => NumberLikeThing.new(4.6)
          assert_template_result "4", "{{ price | round }}", 'price' => NumberLikeThing.new(4.3)
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
