package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SplitTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                { "{{ 'a-b-c' | split:'-' }}", "[a, b, c]" },
                { "{{ 'a-b-c' | split:'' }}", "[a, -, b, -, c]" },
                { "{{ 'a-b-c' | split:'?' }}", "[a-b-c]" },
                { "{{ 'a-b-c' | split:nil }}", "[a, -, b, -, c]" },
        };

        for(String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
