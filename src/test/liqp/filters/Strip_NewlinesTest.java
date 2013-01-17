package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Strip_NewlinesTest {

    @Test
    public void applyTest() throws RecognitionException {

        String json = "{ \"a\" : \"1\\r\\r\\n\\n\\r\\n2\\r3\" }";

        String[][] tests = {
                { "{{ nil | strip_newlines }}", "" },
                { "{{ a | strip_newlines }}", "123" },
        };

        for(String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }
}
