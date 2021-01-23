package liqp.nodes;

import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
                "offset",
                "reversed"
        };

        for (String keyword : keywords) {

            String test = "{{" + keyword + "}}";
            String expected = keyword + "_" + Integer.toString(keyword.length());
            String json = "{\"" + keyword + "\" : \"" + expected + "\" }";
            Template template = Template.parse(test);
            String rendered = template.render(json);

            assertThat(rendered + "=" + expected, rendered, is(expected));
        }
    }

    @Test
    public void badKeywordAsVariableTest() {
        String[][] keywords = {
                {"true", "true"},
                {"false", "false"},
                {"nil", ""},
                {"null", ""},
                {"empty", ""},
                {"blank", ""},
        };

        for (String[] keyword : keywords) {

            String test = "{{" + keyword[0] + "}}";
            String expected = keyword[1];
            String json = "{\"" + keyword[0] + "\" : \"bad\" }";
            Template template = Template.parse(test);
            String rendered = template.render(json);

            String message = test + " --> [" + rendered + "] = [" + expected + "]";
            assertThat(message, rendered, is(expected));
        }
    }

    @Test
    public void testDateWithFilter() {
        // given

        // when
        String res = Template.parse("{{ a | truncate: 13 }}").render(Collections.singletonMap("a", LocalDateTime.parse("2011-12-03T10:15:30")));

        // then
        assertEquals("2011-12-03...", res);
    }

}
