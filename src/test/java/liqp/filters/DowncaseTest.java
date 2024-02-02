package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;

public class DowncaseTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ '' | downcase }}", ""},
                {"{{ nil | downcase }}", ""},
                {"{{ 'Abc' | downcase }}", "abc"},
                {"{{ 'abc' | downcase }}", "abc"},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    /*
	 * def test_downcase
     *   assert_equal 'testing', @filters.downcase("Testing")
     *   assert_equal '', @filters.downcase(nil)
     * end
	 */
    @Test
    public void applyOriginalTest() {

        final Filter filter = Filters.COMMON_FILTERS.get("downcase");
        TemplateContext templateContext = new TemplateContext();
        assertThat(filter.apply(templateContext, "Testing"), is((Object)"testing"));
        assertThat(filter.apply(templateContext, null), is((Object)""));
    }
}
