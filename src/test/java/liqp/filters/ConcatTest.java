package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConcatTest {

    /*
        def test_concat
          assert_equal [1, 2, 3, 4], @filters.concat([1, 2], [3, 4])
          assert_equal [1, 2, 'a'],  @filters.concat([1, 2], ['a'])
          assert_equal [1, 2, 10],   @filters.concat([1, 2], [10])

          assert_raises(Liquid::ArgumentError, "concat filter requires an array argument") do
            @filters.concat([1, 2], 10)
          end
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
