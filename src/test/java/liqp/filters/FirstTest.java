package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;

public class FirstTest {

    @Test
    public void applyTest() throws RecognitionException {

        Template template = TemplateParser.DEFAULT.parse("{{values | first}}");

        String rendered = String.valueOf(template.render("{\"values\" : [\"Mu\", \"foo\", \"bar\"]}"));

        assertThat(rendered, is("Mu"));
    }
    
    @Test
    public void applyObjectTest() {
    	Template template = TemplateParser.DEFAULT.parse("{%- assign product = values | first -%}{{product.title}} {{product.price}}");

        String rendered = String.valueOf(template.render("{\"values\" : [{ \"title\": \"Product 1\", \"price\": 1299 }, { \"title\": \"Product 2\", \"price\": 2999 }]}"));

        assertThat(rendered, is("Product 1 1299"));
    }

    /*
     * def test_first_last
     *   assert_equal 1, @filters.first([1,2,3])
     *   assert_equal 3, @filters.last([1,2,3])
     *   assert_equal nil, @filters.first([])
     *   assert_equal nil, @filters.last([])
     * end
     */
    @Test
    public void applyOriginalTest() {

        Filter filter = Filters.COMMON_FILTERS.get("first");

        TemplateContext context = new TemplateContext();
        assertThat(filter.apply(new Integer[]{1, 2, 3}, context), is((Object)1));
        assertThat(filter.apply(new Integer[]{}, context), is((Object)null));
    }
}
