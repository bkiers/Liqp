package liqp.nodes;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NotNodeTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{% if not true %}TRUE{% else %}FALSE{% endif %}", "FALSE"},
                {"{% if not false %}TRUE{% else %}FALSE{% endif %}", "TRUE"},
                {"{% if false or not true %}TRUE{% else %}FALSE{% endif %}", "FALSE"},
                {"{% if not false and true %}TRUE{% else %}FALSE{% endif %}", "TRUE"},
                {"{% if nil or not true %}TRUE{% else %}FALSE{% endif %}", "FALSE"},
                {"{% if nil or not false %}TRUE{% else %}FALSE{% endif %}", "TRUE"},
                {"{% if '' and not false %}TRUE{% else %}FALSE{% endif %}", "TRUE"},
                {"{% if '' and not true %}TRUE{% else %}FALSE{% endif %}", "FALSE"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
