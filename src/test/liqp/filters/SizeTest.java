package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SizeTest {

    @Test
    public void applyTest() throws RecognitionException {

        String json = "{ \"n\" : [1,2,3,4,5] }";

        String[][] tests = {
                { "{{ nil | size }}", "0" },
                { "{{ 999999999999999 | size }}", "8" },
                { "{{ '1' | size }}", "1" },
                { "{{ N | size }}", "0" },
                { "{{ n | size }}", "5" },
                { "{{ true | size }}", "0" },
        };

        for(String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }
}
