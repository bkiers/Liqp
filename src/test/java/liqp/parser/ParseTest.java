package liqp.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import liqp.Template;
import liqp.TemplateContext;
import org.junit.Test;

import liqp.TemplateParser;
import liqp.exceptions.LiquidException;

public class ParseTest {

    /*
     * def test_error_with_css
     *   text = %| div { font-weight: bold; } |
     *   template = TemplateParser.DEFAULT.parse(text)
     *
     *   assert_equal text, template.render
     *   assert_equal [String], template.root.nodelist.collect {|i| i.class}
     * end
     */
    @Test
    public void error_with_cssTest() throws Exception {

        String text = " div { font-weight: bold; } ";

        assertThat(TemplateParser.DEFAULT.parse(text).render(), is(text));
    }

    /*
     * def test_raise_on_single_close_bracet
     *   assert_raise(SyntaxError) do
     *     TemplateParser.DEFAULT.parse("text {{method} oh nos!")
     *   end
     * end
     */
    @Test(expected=LiquidException.class)
    public void raise_on_single_close_bracetTest() throws Exception {
        TemplateParser.DEFAULT.parse("text {{method} oh nos!");
    }

    /*
     * def test_raise_on_label_and_no_close_bracets
     *   assert_raise(SyntaxError) do
     *     TemplateParser.DEFAULT.parse("TEST {{ ")
     *   end
     * end
     */
    @Test(expected=LiquidException.class)
    public void raise_on_label_and_no_close_bracetsTest() throws Exception {
        TemplateParser.DEFAULT.parse("TEST {{ ");
    }

    /*
     * def test_raise_on_label_and_no_close_bracets_percent
     *   assert_raise(SyntaxError) do
     *     TemplateParser.DEFAULT.parse("TEST {% ")
     *   end
     * end
     */
    @Test(expected=LiquidException.class)
    public void raise_on_label_and_no_close_bracets_percentTest() throws Exception {
        TemplateParser.DEFAULT.parse("TEST {% ");
    }

    /*
     * def test_error_on_empty_filter
     *   assert_nothing_raised do
     *     TemplateParser.DEFAULT.parse("{{test |a|b|}}")
     *     TemplateParser.DEFAULT.parse("{{test}}")
     *     TemplateParser.DEFAULT.parse("{{|test|}}")
     *   end
     * end
     */
    @Test
    public void error_on_empty_filterTest() throws Exception {
        //TemplateParser.DEFAULT.parse("{{test |a|b|}}"); // TODO isn't allowed (yet?)
        TemplateParser.DEFAULT.parse("{{test}}");
        //TemplateParser.DEFAULT.parse("{{|test|}}"); // TODO isn't allowed (yet?)
    }

    /*
     * def test_meaningless_parens
     *   assigns = {'b' => 'bar', 'c' => 'baz'}
     *   markup = "a == 'foo' or (b == 'bar' and c == 'baz') or false"
     *   assert_template_result(' YES ',"{% if #{markup} %} YES {% endif %}", assigns)
     * end
     */
    @Test
    public void meaningless_parensTest() throws Exception {

        String assigns = "{\"b\" : \"bar\", \"c\" : \"baz\"}";
        String markup = "a == 'foo' or (b == 'bar' and c == 'baz') or false";
        assertThat(TemplateParser.DEFAULT.parse("{% if " + markup + " %} YES {% endif %}").render(assigns), is(" YES "));
    }

    /*
     * def test_unexpected_characters_silently_eat_logic
     *   markup = "true && false"
     *   assert_template_result(' YES ',"{% if #{markup} %} YES {% endif %}")
     *   markup = "false || true"
     *   assert_template_result('',"{% if #{markup} %} YES {% endif %}")
     * end
     */
    @Test
    public void unexpected_characters_silently_eat_logicTest() throws Exception {

        //assertThat(TemplateParser.DEFAULT.parse("{% if true && false %} YES {% endif %}").render(), is(" YES ")); // TODO isn't allowed (yet?)

        //assertThat(TemplateParser.DEFAULT.parse("{% if true || false %} YES {% endif %}").render(), is(" YES ")); // TODO isn't allowed (yet?)
    }

    @Test
    public void keywords_as_identifier() throws Exception {

        assertThat(
                TemplateParser.DEFAULT.parse("var2:{{var2}} {%assign var2 = var.comment%} var2:{{var2}}")
                        .render(" { \"var\": { \"comment\": \"content\" } } "),
                is("var2:  var2:content"));

        assertThat(
                TemplateParser.DEFAULT.parse("var2:{{var2}} {%assign var2 = var.end%} var2:{{var2}}")
                        .render(" { \"var\": { \"end\": \"content\" } } "),
                is("var2:  var2:content"));
    }

    @Test
    public void testStripSpaces() {
        String source = "a \n \n {{ a }} \n \n c";

        // default
        String res = new TemplateParser.Builder()
                .build()
                .parse(source).render("a", "b");
        assertEquals("a \n \n b \n \n c", res);


        // false is default
        res = new TemplateParser.Builder()
                .withStripSpaceAroundTags(false)
                .build()
                .parse(source).render("a", "b");
        assertEquals("a \n \n b \n \n c", res);

        // true
        res = new TemplateParser.Builder()
                .withStripSpaceAroundTags(true)
                .build()
                .parse(source).render("a", "b");
        assertEquals("abc", res);

        res = new TemplateParser.Builder()
                .withStripSpaceAroundTags(true)
                .withStripSingleLine(true)
                .build()
                .parse(source).render("a", "b");
        assertEquals("a \n \nb \n c", res);
    }
}
