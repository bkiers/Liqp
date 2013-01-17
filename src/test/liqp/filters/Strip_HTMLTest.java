package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Strip_HTMLTest {

    @Test
    public void applyTest() throws RecognitionException {

        String json = "{ \"html\" : \"1<h>2</h>3\" }";

        String[][] tests = {
                { "{{ nil | strip_html }}", "" },
                { "{{ 456 | strip_html }}", "456" },
                { "{{ '45<6' | strip_html }}", "45<6" },
                { "{{ '<a>' | strip_html }}", "" },
                { "{{ html | strip_html }}", "123" },
        };

        for(String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }
}
