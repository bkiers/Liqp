package liqp.filters;

import liqp.Template;
import liqp.TemplateContext;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CapitalizeTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{'a' | capitalize}}", "A"},
                {"{{'' | capitalize}}", ""},
                {"{{1 | capitalize}}", "1"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    /*
     *
     */
    @Test
    public void applyOriginalTest() {

        Filter filter = Filter.getFilter("capitalize");

        TemplateContext context = new TemplateContext();
        assertThat(filter.apply("testing", context), is((Object)"Testing"));
        assertThat(filter.apply(null, context), is((Object)""));
    }
}
