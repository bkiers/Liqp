package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReplaceTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ '' | replace:'a', 'A' }}", ""},
                {"{{ nil | replace:'a', 'A' }}", ""},
                {"{{ 'aabb' | replace:'ab', 'A' }}", "aAb"},
                {"{{ 'ababab' | replace:'a', 'A' }}", "AbAbAb"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    @Test(expected = RuntimeException.class)
    public void applyTestInvalidPattern1() throws RecognitionException {
        Template.parse("{{ 'ababab' | replace:nil, 'A' }}").render();
    }

    @Test(expected = RuntimeException.class)
    public void applyTestInvalidPattern2() throws RecognitionException {
        Template.parse("{{ 'ababab' | replace:'a', nil }}").render();
    }

}
