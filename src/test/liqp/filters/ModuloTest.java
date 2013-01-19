package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ModuloTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ 8 | modulo: 2 }}", "0"},
                {"{{ 8 | modulo: 3 }}", "2"},
                {"{{ 8 | modulo: 3. }}", "2.0"},
                {"{{ 8 | modulo: 3.0 }}", "2.0"},
                {"{{ 8 | modulo: 2.0 }}", "0.0"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
