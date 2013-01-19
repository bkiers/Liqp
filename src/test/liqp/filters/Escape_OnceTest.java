package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Escape_OnceTest {

    @Test
    public void applyTest() throws RecognitionException {

        String json = "{ \"n\" : [1,2,3,4,5] }";

        String[][] tests = {
                {"{{ nil | escape_once }}", ""},
                {"{{ 42 | escape_once }}", "42"},
                {"{{ n | escape_once }}", "12345"},
                {"{{ '<foo>&\"' | escape_once }}", "&lt;foo&gt;&amp;&quot;"},
                {"{{ false | escape_once }}", "false"},
        };


        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }
}
