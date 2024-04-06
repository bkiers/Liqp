package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateParser;

public class PlusTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ 8 | plus: 2 }}", "10"},
                {"{{ 8 | plus: 3 }}", "11"},
                {"{{ 8 | plus: '3.' }}", "11.0"},
                {"{{ 8 | plus: 3.0 }}", "11.0"},
                {"{{ 8 | plus: \"2.0\" }}", "10.0"},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    @SuppressWarnings("deprecation")
    @Test(expected=RuntimeException.class)
    public void applyTestInvalid1() {
        Filters.COMMON_FILTERS.get("plus").apply(1, null);
    }

    @SuppressWarnings("deprecation")
    @Test(expected=RuntimeException.class)
    public void applyTestInvalid2() {
        Filters.COMMON_FILTERS.get("plus").apply(1, null, 2, 3);
    }

    /*
     * def test_plus
     *   assert_template_result "2", "{{ 1 | plus:1 }}"
     *   assert_template_result "2.0", "{{ '1' | plus:'1.0' }}"
     * end
     */
    @Test
    public void applyOriginalTest() {

        assertThat(TemplateParser.DEFAULT.parse("{{ 1 | plus:1 }}").render(), is((Object)"2"));
        assertThat(TemplateParser.DEFAULT.parse("{{ '1' | plus:'1.0' }}").render(), is((Object)"2.0"));
    }
}
