package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateParser;

public class Newline_To_BrTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ '' | newline_to_br }}", ""},
                {"{{ nil | newline_to_br }}", ""},
                {"{{ 'Line 1\nLine 2' | newline_to_br }}", "Line 1<br />\nLine 2"}
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
