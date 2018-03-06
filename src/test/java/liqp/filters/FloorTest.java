package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FloorTest {

    /*
        def test_floor
          assert_template_result "4", "{{ input | floor }}", 'input' => 4.6
          assert_template_result "4", "{{ '4.3' | floor }}"
          assert_raises(Liquid::FloatDomainError) do
            assert_template_result "4", "{{ 1.0 | divided_by: 0.0 | floor }}"
          end

          assert_template_result "5", "{{ price | floor }}", 'price' => NumberLikeThing.new(5.4)
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
