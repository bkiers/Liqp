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

//        String output = Template.parse("{{ product.to_liquid.texts.text }}").render("product", new ProductDrop());
//        assertThat(output, is("text1"));

        String output = Template.parse("{{ product | map: \"to_liquid\" | map: \"texts\" | map: \"text\" }}").render("product", new ProductDrop());
        assertThat(output, is("text1"));
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

    protected String callmenot() {
        return "protected";
    }
}