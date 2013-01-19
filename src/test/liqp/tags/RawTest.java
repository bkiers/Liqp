package liqp.tags;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RawTest {

    @Test
    public void renderTest() throws RecognitionException {

        String[][] tests = {
                {"{% raw %}{% endraw %}", ""},
                {"{% raw %}{{a|b}}{% endraw %}", "{{a|b}}"}
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
