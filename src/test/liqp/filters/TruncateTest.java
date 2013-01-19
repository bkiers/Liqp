package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TruncateTest {

    @Test
    public void applyTest() throws RecognitionException {

        String json = "{ \"txt\" : \"012345678901234567890123456789012345678901234567890123456789\" }";

        String[][] tests = {
                {"{{ nil | truncate }}", ""},
                {"{{ txt | truncate }}", "01234567890123456789012345678901234567890123456..."},
                {"{{ txt | truncate: 5 }}", "01..."},
                {"{{ txt | truncate: 5, '???' }}", "01???"},
                {"{{ txt | truncate: 500, '???' }}", "012345678901234567890123456789012345678901234567890123456789"},
                {"{{ txt | truncate: 2, '===' }}", "==="},
                {"{{ '12345' | truncate: 4, '===' }}", "1==="}
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }
}
