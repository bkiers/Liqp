package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PlusTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ 8 | plus: 2 }}", "10"},
                {"{{ 8 | plus: 3 }}", "11"},
                {"{{ 8 | plus: 3. }}", "11.0"},
                {"{{ 8 | plus: 3.0 }}", "11.0"},
                {"{{ 8 | plus: 2.0 }}", "10.0"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
