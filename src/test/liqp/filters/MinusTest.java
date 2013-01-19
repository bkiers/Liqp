package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MinusTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ 8 | minus: 2 }}", "6"},
                {"{{ 8 | minus: 3 }}", "5"},
                {"{{ 8 | minus: 3. }}", "5.0"},
                {"{{ 8 | minus: 3.0 }}", "5.0"},
                {"{{ 8 | minus: 2.0 }}", "6.0"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
