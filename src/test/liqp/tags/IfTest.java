package liqp.tags;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IfTest {

    @Test
    public void renderTest() throws RecognitionException {

        String[][] tests = {
                { "{% if user %} Hello {{ user.name }} {% endif %}", "" },
        };

        for(String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }

        String json = "{\"user\" : {\"name\" : \"Tobi\", \"age\" : 42} }";

        tests = new String[][]{
                { "{% if user %} Hello {{ user.name }}! {% endif %}", "Hello Tobi!" },
                { "{% if user.name == 'tobi' %}A{% elsif user.name == 'Tobi' %}B{% endif %}", "B" },
                { "{% if user.name == 'tobi' %}A{% elsif user.name == 'TOBI' %}B{% else %}C{% endif %}", "C" },
        };

        for(String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }
}
