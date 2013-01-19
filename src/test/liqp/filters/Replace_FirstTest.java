package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Replace_FirstTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ '' | replace_first:'a', 'A' }}", ""},
                {"{{ nil | replace_first:'a', 'A' }}", ""},
                {"{{ 'aabbabab' | replace_first:'ab', 'A' }}", "aAbabab"},
                {"{{ 'ababab' | replace_first:'a', 'A' }}", "Ababab"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    @Test(expected = RuntimeException.class)
    public void applyTestInvalidPattern1() throws RecognitionException {
        Template.parse("{{ 'ababab' | replace_first:nil, 'A' }}").render();
    }

    @Test(expected = RuntimeException.class)
    public void applyTestInvalidPattern2() throws RecognitionException {
        Template.parse("{{ 'ababab' | replace_first:'a', nil }}").render();
    }

}
