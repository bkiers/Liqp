package liqp.tags;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UnlessTest {

    @Test
    public void renderTest() throws RecognitionException {

        String json = "{\"user\" : {\"name\" : \"tobi\", \"age\" : 42} }";

        String[][] tests = {
                { "{% unless user.name == 'tobi' %}X{% endunless %}", "" },
                { "{% unless user.name == 'bob' %}X{% endunless %}", "X" },
                { "{% unless user.name == 'tobi' %}X{% else %}Y{% endunless %}", "Y" },
        };

        for(String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }
}
