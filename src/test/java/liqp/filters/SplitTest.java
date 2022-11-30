package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.regex.Pattern;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;

public class SplitTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ 'a-b-c' | split:'-' }}", "abc"},
                {"{{ 'a-b-c' | split:'' }}", "a-b-c"},
                {"{{ 'a-b-c' | split:'?' }}", "a-b-c"},
                {"{{ 'a-b-c' | split:nil }}", "a-b-c"},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    /*
     * def test_strip
     *   assert_equal ['12','34'], @filters.split('12~34', '~')
     *   assert_equal ['A? ',' ,Z'], @filters.split('A? ~ ~ ~ ,Z', '~ ~ ~')
     *   assert_equal ['A?Z'], @filters.split('A?Z', '~')
     *   # Regexp works although Liquid does not support.
     *   assert_equal ['A','Z'], @filters.split('AxZ', /x/)
     * end
     */
    @Test
    public void applyOriginalTest() {
        TemplateContext context = new TemplateContext();
        
        final Filter filter = Filters.COMMON_FILTERS.get("split");

        assertThat(filter.apply("12~34", context, "~"), is((Object)new String[]{"12", "34"}));
        assertThat(filter.apply("A? ~ ~ ~ ,Z", context, "~ ~ ~"), is((Object)new String[]{"A? ", " ,Z"}));
        assertThat(filter.apply("A?Z", context, "~"), is((Object)new String[]{"A?Z"}));
        assertThat(filter.apply("AxZ", context, Pattern.compile("x")), is((Object)new String[]{"A", "Z"}));
    }
}
