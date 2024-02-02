package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.HashMap;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;
import liqp.parser.Inspectable;

public class SizeTest {

    @Test
    public void applyTest() throws RecognitionException {

        String json = "{ \"n\" : [1,2,3,4,5] }";

        String[][] tests = {
                {"{{ nil | size }}", "0"},
                {"{{ 999999999999999 | size }}", "8"},
                {"{{ '1' | size }}", "1"},
                {"{{ N | size }}", "0"},
                {"{{ n | size }}", "5"},
                {"{{ true | size }}", "0"},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }

	/*
     * def test_size
     *   assert_equal 3, @filters.size([1,2,3])
     *   assert_equal 0, @filters.size([])
     *   assert_equal 0, @filters.size(nil)
     * end
     */
    @Test
    public void applyOriginalTest() {

        final Filter filter = Filters.COMMON_FILTERS.get("size");
        TemplateContext context = new TemplateContext();

        assertThat(filter.apply(context, new Integer[]{1, 2, 3}), is( 3));
        assertThat(filter.apply(context, new Object[0]), is(0));
        assertThat(filter.apply(context, null), is(0));
        assertThat(filter.apply(context, new HashMap<>()), is(0));
        assertThat(filter.apply(context, Collections.singletonMap("a", 1)), is(1));
        assertThat(filter.apply(context, new Inspectable() {
            @SuppressWarnings("unused")
            public final String a = "a";
            @SuppressWarnings("unused")
            public final String b = "b";
            @SuppressWarnings("unused")
            public final String c = "c";
            @SuppressWarnings("unused")
            public final String d = "d";
        }), is(4));
    }
}
