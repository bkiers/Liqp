package liqp;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DropTest {

    /*
     * def test_text_drop
     *   output = Liquid::Template.parse( ' {{ product.texts.text }} '  ).render('product' => ProductDrop.new)
     *   assert_equal ' text1 ', output
     * end
     */
    @Test
    public void test_text_drop() {
        String output = Template.parse(" {{ product.texts.text }} ").render("product", new ProductDrop());
        assertThat(output, is(" text1 "));
    }

    /*
     * def test_unknown_method
     *   output = Liquid::Template.parse( ' {{ product.catchall.unknown }} '  ).render('product' => ProductDrop.new)
     *   assert_equal ' method: unknown ', output
     * end
     */
    @Test
    public void test_unknown_method() {
        String output = Template.parse(" {{ product.catchall.unknown }} ").render("product", new ProductDrop());
        assertThat(output, is(" method: unknown "));
    }

    /*
     * def test_drops_respond_to_to_liquid
     *   assert_equal "text1", Liquid::Template.parse("{{ product.to_liquid.texts.text }}").render('product' => ProductDrop.new)
     *   assert_equal "text1", Liquid::Template.parse('{{ product | map: "to_liquid" | map: "texts" | map: "text" }}').render('product' => ProductDrop.new)
     * end
     */
    @Test
    public void test_drops_respond_to_to_liquid() {

        String output = Template.parse("{{ product.to_liquid.texts.text }}").render("product", new ProductDrop());
        assertThat(output, is("text1"));

        output = Template.parse("{{ product | map: \"to_liquid\" | map: \"texts\" | map: \"text\" }}").render("product", new ProductDrop());
        assertThat(output, is("text1"));
    }

    /*
     *  def test_integer_argument_drop
     *   output = Liquid::Template.parse( ' {{ product.catchall[8] }} '  ).render('product' => ProductDrop.new)
     *   assert_equal ' method: 8 ', output
     * end
     */
    @Test
    public void test_integer_argument_drop() {
        String output = Template.parse(" {{ product.catchall[8] }} ").render("product", new ProductDrop());
        assertThat(output, is(" method: 8 "));
    }

    /*
     * def test_text_array_drop
     *   output = Liquid::Template.parse( '{% for text in product.texts.array %} {{text}} {% endfor %}'  ).render('product' => ProductDrop.new)
     *   assert_equal ' text1  text2 ', output
     * end
     */
    @Test
    public void test_text_array_drop() {
        String output = Template.parse("{% for text in product.texts.array %} {{text}} {% endfor %}").render("product", new ProductDrop());
        assertThat(output, is(" text1  text2 "));
    }

    /*
     * def test_nested_context_drop
     *   output = Liquid::Template.parse( ' {{ product.context.foo }} '  ).render('product' => ProductDrop.new, 'foo' => "monkey")
     *   assert_equal ' monkey ', output
     * end
     */
    @Test
    public void test_nested_context_drop() {
        String output = Template.parse(" {{ product.context.foo }} ").render("product", new ProductDrop(), "foo", "monkey");
        assertThat(output, is(" monkey "));
    }

    /*
     * def test_protected
     *   output = Liquid::Template.parse( ' {{ product.callmenot }} '  ).render('product' => ProductDrop.new)
     *   assert_equal '  ', output
     * end
     */
    @Test
    public void test_protected() {
    }

    /*
     * def test_object_methods_not_allowed
     *   [:dup, :clone, :singleton_class, :eval, :class_eval, :inspect].each do |method|
     *     output = Liquid::Template.parse(" {{ product.#{method} }} ").render('product' => ProductDrop.new)
     *     assert_equal '  ', output
     *   end
     * end
     */
    @Test
    public void test_object_methods_not_allowed() {
    }
}

class ProductDrop extends Drop {

    static class TextDrop extends Drop {
        public String[] array() {
            return new String[]{"text1", "text2"};
        }
        public String text() {
            return "text1";
        }
    }

    static class CatchallDrop extends Drop {
        @Override
        public Object before_method(String method) {
            return "method: " + method;
        }
    }

    public Drop texts() {
        return new TextDrop();
    }

    public Drop catchall() {
        return new CatchallDrop();
    }

    public Drop context() {
        return new ContextDrop();
    }

    protected String callmenot() {
        return "protected";
    }
}

class ContextDrop extends Drop {

    public int scopes() {
        return super.context == null ? 0 : super.context.size();
    }

    public Object[] scopes_as_array() {
        return super.context == null ? new Object[0] : super.context.values().toArray(new Object[super.context.size()]);
    }

    public Object loop_pos() {
        return super.context == null ? -1 : super.context.get("forloop.index");
    }

    public Object before_method(String method) {
        return super.context == null ? null : super.context.get(method);
    }
}