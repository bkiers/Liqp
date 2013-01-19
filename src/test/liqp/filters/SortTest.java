package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SortTest {

    @Test
    public void applyTest() throws RecognitionException {

        String json = "{ \"words\" : [\"2\", \"13\", \"1\"], \"numbers\" : [2, 13, 1] }";

        String[][] tests = {
                {"{{ x | sort }}", ""},
                {"{{ words | sort }}", "1132"},
                {"{{ numbers | sort }}", "1213"},
                {"{{ numbers | sort | last }}", "13"},
                {"{{ numbers | sort | first }}", "1"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }
}
