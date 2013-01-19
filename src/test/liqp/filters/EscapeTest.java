package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EscapeTest {

    @Test
    public void applyTest() throws RecognitionException {

        String json = "{ \"n\" : [1,2,3,4,5] }";

        String[][] tests = {
                {"{{ nil | escape }}", ""},
                {"{{ 42 | escape }}", "42"},
                {"{{ n | escape }}", "12345"},
                {"{{ '<foo>&\"' | escape }}", "&lt;foo&gt;&amp;&quot;"},
                {"{{ false | escape }}", "false"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }
}
