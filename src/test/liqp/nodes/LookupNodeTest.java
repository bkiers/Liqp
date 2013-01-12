package liqp.nodes;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LookupNodeTest {

    @Test
    public void renderTest() throws RecognitionException {

        String json = "{\"a\" : { \"b\" : { \"c\" : 42 } } }";

        String[][] tests = {
                { "{{a.b.c.d}}", "" },
                { "{{a.b.c}}", "42" },
        };

        for(String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }
}
