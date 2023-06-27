package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateParser;

public class UniqTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ x | uniq }}", "", "{ \"x\": [] }"},
                {"{{ x | uniq }}", "true", "{ \"x\": true }"},
                {"{{ x | uniq }}", "mu", "{ \"x\": \"mu\" }"},
                {"{{ x | uniq }}", "", "{ \"x\": null }"},
                {"{{ x | uniq }}", "1342", "{ \"x\": [1, 1, 3, 4, 3, 2, 1, 2, 3, 2, 1, 1, 2] }"},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render(test[2]));

            assertThat(rendered, is(test[1]));
        }
    }
}
