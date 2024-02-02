package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;

public class ReplaceTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ '' | replace:'a', 'A' }}", ""},
                {"{{ nil | replace:'a', 'A' }}", ""},
                {"{{ 'aabb' | replace:'ab', 'A' }}", "aAb"},
                {"{{ 'ababab' | replace:'a', 'A' }}", "AbAbAb"},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    @Test(expected = RuntimeException.class)
    public void applyTestInvalidPattern1() throws RecognitionException {
        TemplateParser.DEFAULT.parse("{{ 'ababab' | replace:nil, 'A' }}").render();
    }

    @Test(expected = RuntimeException.class)
    public void applyTestInvalidPattern2() throws RecognitionException {
        TemplateParser.DEFAULT.parse("{{ 'ababab' | replace:'a', nil }}").render();
    }

    /*
     * def test_replace
     *   assert_equal 'b b b b', @filters.replace("a a a a", 'a', 'b')
     *   assert_equal 'b a a a', @filters.replace_first("a a a a", 'a', 'b')
     *   assert_template_result 'b a a a', "{{ 'a a a a' | replace_first: 'a', 'b' }}"
     * end
     */
    @Test
    public void applyOriginalTest() {
        TemplateContext context = new TemplateContext();
        Filter filter = Filters.COMMON_FILTERS.get("replace");

        assertThat(filter.apply(context, "a a a a", "a", "b"), is((Object)"b b b b"));
    }
}
