package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;

public class ModuloTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ 8 | modulo: 2 }}", "0"},
                {"{{ 8 | modulo: 3 }}", "2"},
                {"{{ \"8\" | modulo: 3. }}", "2.0"},
                {"{{ 8 | modulo: 3.0 }}", "2.0"},
                {"{{ 8 | modulo: '2.0' }}", "0.0"},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    @Test(expected=RuntimeException.class)
    public void applyTestInvalid1() {
        TemplateContext context = new TemplateContext();
        Filters.COMMON_FILTERS.get("modulo").apply(1, context);
    }

    @Test(expected=RuntimeException.class)
    public void applyTestInvalid2() {
        TemplateContext context = new TemplateContext();
        Filters.COMMON_FILTERS.get("modulo").apply(1, context, 2, 3);
    }


    /*
     * def test_modulo
     *   assert_template_result "1", "{{ 3 | modulo:2 }}"
     * end
     */
    @Test
    public void applyOriginalTest() {

        assertThat(TemplateParser.DEFAULT.parse("{{ 3 | modulo:2 }}").render(), is((Object)"1"));
    }

    @Test
    public void testModuloWithFloated() {
        assertThat(TemplateParser.DEFAULT.parse("{{ 183.357 | modulo: 12 }}").render(), is((Object)"3.357"));
    }
}
