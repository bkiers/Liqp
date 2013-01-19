package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JoinTest {

    @Test
    public void applyTest() throws RecognitionException {

        String json = "{ \"array\" : [\"x\", \"y\", \"z\"] }";

        String[][] tests = {
                {"{{ array | join }}", "x y z"},
                {"{{ array | join:'' }}", "xyz"},
                {"{{ array | join:'@@@' }}", "x@@@y@@@z"},
                {"{{ x | join:'@@@' }}", ""},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }
}
