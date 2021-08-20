package liqp.filters;

import liqp.Template;
import liqp.TemplateContext;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UpcaseTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ '' | upcase }}", ""},
                {"{{ nil | upcase }}", ""},
                {"{{ 'Abc' | upcase }}", "ABC"},
                {"{{ 'abc' | upcase }}", "ABC"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    /*
     * def test_upcase
     *   assert_equal 'TESTING', @filters.upcase("Testing")
     *   assert_equal '', @filters.upcase(nil)
     * end
     */
    @Test
    public void applyOriginalTest() {
        TemplateContext context = new TemplateContext();
        final Filter filter = Filter.getFilter("upcase");

        assertThat(filter.apply("Testing", context), is((Object)"TESTING"));
        assertThat(filter.apply(null, context), is((Object)""));
    }
}
