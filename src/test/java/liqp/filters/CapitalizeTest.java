package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;

public class CapitalizeTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{'a' | capitalize}}", "A"},
                {"{{'' | capitalize}}", ""},
                {"{{1 | capitalize}}", "1"},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    /*
     *
     */
    @Test
    public void applyOriginalTest() {

        Filter filter = Filters.COMMON_FILTERS.get("capitalize");

        TemplateContext context = new TemplateContext();
        assertThat(filter.apply(context, "testing"), is((Object)"Testing"));
        assertThat(filter.apply(context, null), is((Object)""));
    }
}
