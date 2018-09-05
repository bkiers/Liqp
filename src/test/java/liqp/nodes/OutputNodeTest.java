package liqp.nodes;

import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OutputNodeTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ X }}", "mu"},
                {"{{ 'a.b.c' | split:'.' | first | upcase }}", "A"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render("{\"X\" : \"mu\"}"));

            assertThat(rendered, is(test[1]));
        }
    }

    @Test
    public void allowedKeywordAsVariableTest() {
        String[] keywords = {
                "capture",
                "endcapture",
                "comment",
                "endcomment",
                "raw",
                "endraw",
                "if",
                "elsif",
                "endif",
                "unless",
                "endunless",
                "else",
                "contains",
                "case",
                "endcase",
                "when",
                "cycle",
                "for",
                "endfor",
                "in",
                "and",
                "or",
                "tablerow",
                "endtablerow",
                "assign",
                "include",
                "with",
                "end",
                "break",
                "continue",
        };

        for (String keyword : keywords) {

            String test = "{{" + keyword + "}}";
            String expected = keyword + "_" + Integer.toString(keyword.length());
            String json = "{\"" + keyword + "\" : \"" + expected + "\" }";
            Template template = Template.parse(test);
            String rendered = template.render(json);

            assertThat(rendered, is(expected));
        }
    }

    @Test
    public void badKeywordAsVariableTest() {
        String[][] keywords = {
                {"true", "true"},
                {"false", "false"},
                {"nil", ""},
                {"null", ""},
                // {"empty", "Object"},
        };

        for (String[] keyword : keywords) {

            String test = "{{" + keyword[0] + "}}";
            String expected = keyword[1];
            String json = "{\"" + keyword[0] + "\" : \"bad\" }";
            Template template = Template.parse(test);
            String rendered = template.render(json);

            assertThat(rendered, is(expected));
        }
    }
}
