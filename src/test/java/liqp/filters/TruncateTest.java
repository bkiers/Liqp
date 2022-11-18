package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;

public class TruncateTest {

    @Test
    public void applyTest() throws RecognitionException {

        String json = "{ \"txt\" : \"012345678901234567890123456789012345678901234567890123456789\" }";

        String[][] tests = {
                {"{{ nil | truncate }}", ""},
                {"{{ txt | truncate }}", "01234567890123456789012345678901234567890123456..."},
                {"{{ txt | truncate: 5 }}", "01..."},
                {"{{ txt | truncate: 5, '???' }}", "01???"},
                {"{{ txt | truncate: 500, '???' }}", "012345678901234567890123456789012345678901234567890123456789"},
                {"{{ txt | truncate: 2, '===' }}", "==="},
                {"{{ '12345' | truncate: 4, '===' }}", "1==="},
                {"{{ 'abc' | truncate: 5 }}", "abc"},
                {"{{ 'abc' | truncate: 2, '-----' }}", "-----"}
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }

    /*
     * def test_truncate
     *   assert_equal '1234...', @filters.truncate('1234567890', 7)
     *   assert_equal '1234567890', @filters.truncate('1234567890', 20)
     *   assert_equal '...', @filters.truncate('1234567890', 0)
     *   assert_equal '1234567890', @filters.truncate('1234567890')
     * end
     */
    @Test
    public void applyOriginalTest() {
        TemplateContext context = new TemplateContext();
        final Filter filter = Filters.COMMON_FILTERS.get("truncate");

        assertThat(filter.apply("1234567890", context, 7), is((Object)"1234..."));
        assertThat(filter.apply("1234567890", context, 20), is((Object)"1234567890"));
        assertThat(filter.apply("1234567890", context, 0), is((Object)"..."));
        assertThat(filter.apply("1234567890", context), is((Object)"1234567890"));
    }
}
