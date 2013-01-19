package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TimesTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ 8 | times: 2 }}", "16"},
                {"{{ 8 | times: 3 }}", "24"},
                {"{{ 8 | times: 3. }}", "24.0"},
                {"{{ 8 | times: 3.0 }}", "24.0"},
                {"{{ 8 | times: 2.0 }}", "16.0"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
