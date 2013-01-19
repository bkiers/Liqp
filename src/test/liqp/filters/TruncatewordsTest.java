package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TruncatewordsTest {

    @Test
    public void applyTest() throws RecognitionException {

        String json = "{ \"txt\" : \"a        b c d e f g h i j a b c d e f g h i j\" }";

        String[][] tests = {
                {"{{ nil | truncatewords }}", ""},
                {"{{ txt | truncatewords }}", "a b c d e f g h i j a b c d e..."},
                {"{{ txt | truncatewords: 5 }}", "a b c d e..."},
                {"{{ txt | truncatewords: 5, '???' }}", "a b c d e???"},
                {"{{ txt | truncatewords: 500, '???' }}", "a        b c d e f g h i j a b c d e f g h i j"},
                {"{{ txt | truncatewords: 2, '===' }}", "a b==="},
                {"{{ txt | truncatewords: 19, '===' }}", "a b c d e f g h i j a b c d e f g h i==="},
                {"{{ txt | truncatewords: 20, '===' }}", "a b c d e f g h i j a b c d e f g h i j==="},
                {"{{ txt | truncatewords: 21, '===' }}", "a        b c d e f g h i j a b c d e f g h i j"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }
}
