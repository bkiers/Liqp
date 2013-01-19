package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MapTest {

    @Test
    public void applyTest() throws RecognitionException {

        String json = "{\"products\" : [\n" +
                "  {\"name\" : \"C\", \"price\" : 1}, \n" +
                "  {\"name\" : \"A\", \"price\" : 3},\n" +
                "  {\"name\" : \"B\", \"price\" : 2}\n" +
                "]}";

        String[][] tests = {
                {"{{ mu | map:'name' }}", ""},
                {"{{ products | map:'XYZ' }}", ""},
                {"{{ products | map:'XYZ' | sort | join }}", ""},
                {"{{ products | map:'name' | sort | join }}", "A B C"},
                {"{{ products | map:'price' | sort | join:'=' }}", "1=2=3"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }
}
