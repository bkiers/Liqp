package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SizeTest {

    @Test
    public void sizeTest() throws RecognitionException {

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
	public void sizeTestLiquid() {

        final String tagName = "size";

        assertThat(Filter.getFilter(tagName).apply(new Integer[]{1, 2, 3}), is((Object)3));
        assertThat(Filter.getFilter(tagName).apply(new Object[0]), is((Object)0));
        assertThat(Filter.getFilter(tagName).apply(null), is((Object)0));
	}
}
