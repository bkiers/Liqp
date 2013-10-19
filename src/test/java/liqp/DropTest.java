package liqp;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DropTest {

    /*
     * def test_product_drop
     *   assert_nothing_raised do
     *     tpl = Liquid::Template.parse( '  '  )
     *     tpl.render('product' => ProductDrop.new)
     *   end
     * end
     */
    @Test
    public void test_product_drop() {
        Template.parse("  ").render("product", new ProductDrop());
    }

    /* def test_drop_does_only_respond_to_whitelisted_methods
     *   assert_equal "", Liquid::Template.parse("{{ product.inspect }}").render('product' => ProductDrop.new)
     *   assert_equal "", Liquid::Template.parse("{{ product.pretty_inspect }}").render('product' => ProductDrop.new)
     *   assert_equal "", Liquid::Template.parse("{{ product.whatever }}").render('product' => ProductDrop.new)
     *   assert_equal "", Liquid::Template.parse('{{ product | map: "inspect" }}').render('product' => ProductDrop.new)
     *   assert_equal "", Liquid::Template.parse('{{ product | map: "pretty_inspect" }}').render('product' => ProductDrop.new)
     *   assert_equal "", Liquid::Template.parse('{{ product | map: "whatever" }}').render('product' => ProductDrop.new)
     * end
     */
    @Test
    public void test_drop_does_only_respond_to_whitelisted_methods() {
        assertThat(Template.parse("{{ product.inspect }}").render("product", new ProductDrop()), is(""));
        assertThat(Template.parse("{{ product.pretty_inspect }}").render("product", new ProductDrop()), is(""));
        assertThat(Template.parse("{{ product.whatever }}").render("product", new ProductDrop()), is(""));
        assertThat(Template.parse("{{ product | map: \"inspect\" }}").render("product", new ProductDrop()), is(""));
        assertThat(Template.parse("{{ product | map: \"pretty_inspect\" }}").render("product", new ProductDrop()), is(""));
        assertThat(Template.parse("{{ product | map: \"whatever\" }}").render("product", new ProductDrop()), is(""));
    }

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
     * def test_context_drop
     *   output = Liquid::Template.parse( ' {{ context.bar }} '  ).render('context' => ContextDrop.new, 'bar' => "carrot")
     *   assert_equal ' carrot ', output
     * end
     */
    @Test
    public void test_context_drop() {
        String output = Template.parse(" {{ context.bar }} ").render("context", new ContextDrop(), "bar", "carrot");
        assertThat(output, is(" carrot "));
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
        String output = Template.parse(" {{ product.callmenot }} ").render("product", new ProductDrop());
        assertThat(output, is("  "));
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

        String[] superMethods = { "hashCode", "toString", "clone", "wait", "notify" };

        for (String method : superMethods) {
            String output = Template.parse(String.format(" {{ product.%s }} ", method)).render("product", new ProductDrop());
            assertThat(output, is("  "));
        }
    }

    /*
     * def test_scope
     *   assert_equal '1', Liquid::Template.parse( '{{ context.scopes }}'  ).render('context' => ContextDrop.new)
     *   assert_equal '2', Liquid::Template.parse( '{%for i in dummy%}{{ context.scopes }}{%endfor%}'  ).render('context' => ContextDrop.new, 'dummy' => [1])
     *   assert_equal '3', Liquid::Template.parse( '{%for i in dummy%}{%for i in dummy%}{{ context.scopes }}{%endfor%}{%endfor%}'  ).render('context' => ContextDrop.new, 'dummy' => [1])
     * end
     */
    @Test
    public void test_scope() {
        assertThat(Template.parse("{{ context.scopes }}").render("context", new ContextDrop()), is("1"));

        // TODO model the scopes as liquid does it. At the moment, there is no separate scope for `{%for ... %}` statements.
        //assertThat(Template.parse("{%for i in dummy%}{{ context.scopes }}{%endfor%}").render("context", new ContextDrop(), "dummy", new Integer[]{1}), is("2"));
        //assertThat(Template.parse("{%for i in dummy%}{%for i in dummy%}{{ context.scopes }}{%endfor%}{%endfor%}").render("context", new ContextDrop(), "dummy", new Integer[]{1}), is("3"));
    }

    /*
     * def test_scope_with_assigns
     *   assert_equal 'variable', Liquid::Template.parse( '{% assign a = "variable"%}{{a}}'  ).render('context' => ContextDrop.new)
     *   assert_equal 'variable', Liquid::Template.parse( '{% assign a = "variable"%}{%for i in dummy%}{{a}}{%endfor%}'  ).render('context' => ContextDrop.new, 'dummy' => [1])
     *   assert_equal 'test', Liquid::Template.parse( '{% assign header_gif = "test"%}{{header_gif}}'  ).render('context' => ContextDrop.new)
     *   assert_equal 'test', Liquid::Template.parse( "{% assign header_gif = 'test'%}{{header_gif}}"  ).render('context' => ContextDrop.new)
     * end
     */
    @Test
    public void test_scope_with_assigns() {
        // TODO
    }

    /*
     * def test_scope_from_tags
     *   assert_equal '1', Liquid::Template.parse( '{% for i in context.scopes_as_array %}{{i}}{% endfor %}'  ).render('context' => ContextDrop.new, 'dummy' => [1])
     *   assert_equal '12', Liquid::Template.parse( '{%for a in dummy%}{% for i in context.scopes_as_array %}{{i}}{% endfor %}{% endfor %}'  ).render('context' => ContextDrop.new, 'dummy' => [1])
     *   assert_equal '123', Liquid::Template.parse( '{%for a in dummy%}{%for a in dummy%}{% for i in context.scopes_as_array %}{{i}}{% endfor %}{% endfor %}{% endfor %}'  ).render('context' => ContextDrop.new, 'dummy' => [1])
     * end
     */
    @Test
    public void test_scope_from_tags() {
        // TODO
    }

    /*
     * def test_access_context_from_drop
     *   assert_equal '123', Liquid::Template.parse( '{%for a in dummy%}{{ context.loop_pos }}{% endfor %}'  ).render('context' => ContextDrop.new, 'dummy' => [1,2,3])
     * end
     */
    @Test
    public void test_access_context_from_drop() {
        assertThat(
                Template.parse("{%for a in dummy%}{{ context.loop_pos }}{% endfor %}").render("context", new ContextDrop(), "dummy", new Integer[]{1, 2, 3}),
                is("123"));
    }

    /*
     * def test_enumerable_drop
     *   assert_equal '123', Liquid::Template.parse( '{% for c in collection %}{{c}}{% endfor %}').render('collection' => EnumerableDrop.new)
     * end
     */
    @Test
    public void test_enumerable_drop() {
        // TODO
    }

    /*
     * def test_enumerable_drop_size
     *   assert_equal '3', Liquid::Template.parse( '{{collection.size}}').render('collection' => EnumerableDrop.new)
     * end
     */
    @Test
    public void test_enumerable_drop_size() {
        // TODO
    }

    /*
     * def test_enumerable_drop_will_invoke_before_method_for_clashing_method_names
     *   ["select", "each", "map", "cycle"].each do |method|
     *     assert_equal method.to_s, Liquid::Template.parse("{{collection.#{method}}}").render('collection' => EnumerableDrop.new)
     *     assert_equal method.to_s, Liquid::Template.parse("{{collection[\"#{method}\"]}}").render('collection' => EnumerableDrop.new)
     *     assert_equal method.to_s, Liquid::Template.parse("{{collection.#{method}}}").render('collection' => RealEnumerableDrop.new)
     *     assert_equal method.to_s, Liquid::Template.parse("{{collection[\"#{method}\"]}}").render('collection' => RealEnumerableDrop.new)
     *   end
     * end
     */
    @Test
    public void test_enumerable_drop_will_invoke_before_method_for_clashing_method_names() {
        // TODO
    }

    /*
     * def test_some_enumerable_methods_still_get_invoked
     *   [ :count, :max ].each do |method|
     *     assert_equal "3", Liquid::Template.parse("{{collection.#{method}}}").render('collection' => RealEnumerableDrop.new)
     *     assert_equal "3", Liquid::Template.parse("{{collection[\"#{method}\"]}}").render('collection' => RealEnumerableDrop.new)
     *     assert_equal "3", Liquid::Template.parse("{{collection.#{method}}}").render('collection' => EnumerableDrop.new)
     *     assert_equal "3", Liquid::Template.parse("{{collection[\"#{method}\"]}}").render('collection' => EnumerableDrop.new)
     *   end
     *
     *   assert_equal "yes", Liquid::Template.parse("{% if collection contains 3 %}yes{% endif %}").render('collection' => RealEnumerableDrop.new)
     *
     *   [ :min, :first ].each do |method|
     *     assert_equal "1", Liquid::Template.parse("{{collection.#{method}}}").render('collection' => RealEnumerableDrop.new)
     *     assert_equal "1", Liquid::Template.parse("{{collection[\"#{method}\"]}}").render('collection' => RealEnumerableDrop.new)
     *     assert_equal "1", Liquid::Template.parse("{{collection.#{method}}}").render('collection' => EnumerableDrop.new)
     *     assert_equal "1", Liquid::Template.parse("{{collection[\"#{method}\"]}}").render('collection' => EnumerableDrop.new)
     *   end
     * end
     */
    @Test
    public void test_some_enumerable_methods_still_get_invoked() {
        // TODO
    }

    /*
     * def test_empty_string_value_access
     *   assert_equal '', Liquid::Template.parse('{{ product[value] }}').render('product' => ProductDrop.new, 'value' => '')
     * end
     *
     * def test_nil_value_access
     *   assert_equal '', Liquid::Template.parse('{{ product[value] }}').render('product' => ProductDrop.new, 'value' => nil)
     * end
     */
    @Test
    public void test_empty_string_value_access() {
        // TODO
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