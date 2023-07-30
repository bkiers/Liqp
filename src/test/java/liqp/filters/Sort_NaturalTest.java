package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateParser;

public class Sort_NaturalTest {

    /*
        def test_sort_natural_empty_array
          assert_equal [], @filters.sort_natural([], "a")
        end
    */
    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ nil | sort_natural }}", "", "{ \"x\": [] }"},
                {"{{ false | sort_natural }}", "false", "{ \"x\": [] }"},
                {"{{ x | sort_natural }}", "42", "{ \"x\": 42 }"},
                {"{{ x | sort_natural }}", "", "{ \"x\": [] }"},
                {"{{ x | sort_natural }}", "99 A00000.0001 a01.1 a04 a1 a10 ", "{ \"x\": [\"a1 \", \"A00000.0001 \", \"a01.1 \", \"a10 \", \"a04 \", \"99 \"] }"},
                {"{{ x | sort_natural }}", "01 02 10 100 A b Cccc cccccccc d Ddd ", "{ \"x\": [\"b \", \"A \", \"Cccc \", \"cccccccc \", \"Ddd \", \"d \", \"01 \", \"02 \", \"10 \", \"100 \"] }"}
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render(test[2]));

            assertThat(rendered, is(test[1]));
        }
    }
}
