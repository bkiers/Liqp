package liqp.tags;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CaseTest {

    @Test
    public void renderTest() throws RecognitionException {

        String json = "{\"x\" : 2, \"y\" : null, \"template\" : \"product\" }";

        String[][] tests = {
                {"{% case x %}{% when 2 %}a{% endcase %}", "a"},
                {"{% case x %}{% when 1 %}a{% when 2 %}b{% else %}c{% endcase %}", "b"},
                {"{% case y %}{% when 1 %}a{% when 2 %}b{% else %}c{% endcase %}", "c"},
                {"{% case template %}{% when '1' %}a{% when 'product' %}P{% else %}c{% endcase %}", "P"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }
}
