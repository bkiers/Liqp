package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class appendTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                { "{{'a' | append: 'b'}}", "ab" },
                { "{{'' | append: ''}}", "" },
                { "{{1 | append: 23}}", "123" },
        };

        for(String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
