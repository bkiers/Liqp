package liqp.filters;

import liqp.Template;
import liqp.TemplateContext;
import liqp.parser.Inspectable;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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

            Template template = Template.parse(test[0]);
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

        final Filter filter = Filter.getFilter("size");
        TemplateContext context = new TemplateContext();

        assertThat(filter.apply(new Integer[]{1, 2, 3}, context), is( 3));
        assertThat(filter.apply(new Object[0], context), is(0));
        assertThat(filter.apply(null, context), is(0));
        assertThat(filter.apply(new HashMap<>(), context), is(0));
        assertThat(filter.apply(Collections.singletonMap("a", 1), context), is(1));
        assertThat(filter.apply(new Inspectable() {
            public final String a = "a";
            public final String b = "b";
            public final String c = "c";
            public final String d = "d";
        }, context), is(4));
    }
}
