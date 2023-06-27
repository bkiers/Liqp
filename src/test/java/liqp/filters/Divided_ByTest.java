package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateParser;

public class Divided_ByTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ 8 | divided_by: 2 }}", "4"},
                {"{{ 8 | divided_by: 3 }}", "2"},
                {"{{ 8 | divided_by: 3. }}", String.valueOf(8 / 3.0)},
                {"{{ 8 | divided_by: 3.0 }}", String.valueOf(8 / 3.0)},
                {"{{ 8 | divided_by: 2.0 }}", "4.0"},
                {"{{ 0 | divided_by: 2.0 }}", "0.0"},
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
        Filters.COMMON_FILTERS.get("divided_by").apply(1);
    }

    @SuppressWarnings("deprecation")
    @Test(expected=RuntimeException.class)
    public void applyTestInvalid2() {
        Filters.COMMON_FILTERS.get("divided_by").apply(1, 2, 3);
    }

    @SuppressWarnings("deprecation")
    @Test(expected=RuntimeException.class)
    public void applyTestInvalid3() {
        Filters.COMMON_FILTERS.get("divided_by").apply(15L, 0L);
    }

    /*
     * def test_divided_by
     *   assert_template_result "4", "{{ 12 | divided_by:3 }}"
     *   assert_template_result "4", "{{ 14 | divided_by:3 }}"
     *
     *   # Ruby v1.9.2-rc1, or higher, backwards compatible Float test
     *   assert_match(/4\.(6{13,14})7/, TemplateParser.DEFAULT.parse("{{ 14 | divided_by:'3.0' }}").render)
     *
     *   assert_template_result "5", "{{ 15 | divided_by:3 }}"
     *   assert_template_result "Liquid error: divided by 0", "{{ 5 | divided_by:0 }}"
     * end
     */
    @SuppressWarnings("deprecation")
    @Test
    public void applyOriginalTest() {

        Filter filter = Filters.COMMON_FILTERS.get("divided_by");

        assertThat(filter.apply(12L, 3L), is((Object)4L));
        assertThat(filter.apply(14L, 3L), is((Object)4L));
        assertTrue(String.valueOf(filter.apply(14L, 3.0)).matches("4[,.]6{10,}7"));

        // see: applyTestInvalid3()
        // assert_template_result "Liquid error: divided by 0", "{{ 5 | divided_by:0 }}"
    }

}
