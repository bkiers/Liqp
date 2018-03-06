package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CompactTest {

    /*
        def test_compact_empty_array
          assert_equal [], @filters.compact([], "a")
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
