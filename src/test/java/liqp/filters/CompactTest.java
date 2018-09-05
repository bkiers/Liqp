package liqp.filters;

import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CompactTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ values | compact }}", "", "{ \"values\": [] }"},
                {"{{ values | compact }}", "123", "{ \"values\": [1, 2, 3] }"},
                {"{{ values | compact }}", "123", "{ \"values\": [\"1\", \"2\", \"3\"] }"},
                {"{{ values | compact }}", "123", "{ \"values\": [null, \"1\", \"\", \"2\", null, \"3\"] }"}
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(test[2]));

            assertThat(rendered, is(test[1]));
        }
    }
}
