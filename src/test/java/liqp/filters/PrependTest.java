package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateParser;

public class PrependTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ 'a' | prepend: 'b' }}", "ba"},
                {"{{ '' | prepend: '' }}", ""},
                {"{{ 1 | prepend: 23 }}", "231"},
                {"{{ nil | prepend: 'a' }}", "a"},
                {"{{ nil | prepend: nil }}", ""},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    /*
     * def test_prepend
     *   assigns = {'a' => 'bc', 'b' => 'a' }
     *   assert_template_result('abc',"{{ a | prepend: 'a'}}",assigns)
     *   assert_template_result('abc',"{{ a | prepend: b}}",assigns)
     * end
     */
    @Test
    public void applyOriginalTest() {

        final String json = "{ \"a\":\"bc\", \"b\":\"a\" }";

        assertThat(TemplateParser.DEFAULT.parse("{{ a | prepend: 'a'}}").render(json), is("abc"));
        assertThat(TemplateParser.DEFAULT.parse("{{ a | prepend: b}}").render(json), is("abc"));
    }
}
