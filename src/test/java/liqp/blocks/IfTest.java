package liqp.blocks;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.Collections;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateParser;
import liqp.TemplateParser.StrictVariablesMode;
import liqp.exceptions.LiquidException;
import liqp.exceptions.VariableNotExistException;

public class IfTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{% if user %} Hello {{ user.name }} {% endif %}", ""},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(test[0] + " = " + test[1], rendered, is(test[1]));
        }

        String json = "{\"user\" : {\"name\" : \"Tobi\", \"age\" : 42} }";

        tests = new String[][]{
                {"{% if user %}Hello {{ user.name }}!{% endif %}", "Hello Tobi!"},
                {"{% if user.name == 'tobi' %}A{% elsif user.name == 'Tobi' %}B{% endif %}", "B"},
                {"{% if user.name == 'tobi' %}A{% elsif user.name == 'TOBI' %}B{% else %}C{% endif %}", "C"},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(test[0] + "(" + json + ") = " + test[1],rendered, is(test[1]));
        }
    }

    /*
     * def test_if
     *   assert_template_result('  ',' {% if false %} this text should not go into the output {% endif %} ')
     *   assert_template_result('  this text should go into the output  ',
     *                          ' {% if true %} this text should go into the output {% endif %} ')
     *   assert_template_result('  you rock ?','{% if false %} you suck {% endif %} {% if true %} you rock {% endif %}?')
     * end
     */
    @Test
    public void ifTest() throws RecognitionException {

        assertThat(TemplateParser.DEFAULT.parse(" {% if false %} this text should not go into the output {% endif %} ").render(), is("  "));
        assertThat(TemplateParser.DEFAULT.parse(" {% if true %} this text should go into the output {% endif %} ").render(), is("  this text should go into the output  "));
        assertThat(TemplateParser.DEFAULT.parse("{% if false %} you suck {% endif %} {% if true %} you rock {% endif %}?").render(), is("  you rock ?"));
    }

    /*
     * def test_if_else
     *   assert_template_result(' YES ','{% if false %} NO {% else %} YES {% endif %}')
     *   assert_template_result(' YES ','{% if true %} YES {% else %} NO {% endif %}')
     *   assert_template_result(' YES ','{% if "foo" %} YES {% else %} NO {% endif %}')
     * end
     */
    @Test
    public void if_elseTest() throws RecognitionException {

        assertThat(TemplateParser.DEFAULT.parse("{% if false %} NO {% else %} YES {% endif %}").render(), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if true %} YES {% else %} NO {% endif %}").render(), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if \"foo\" %} YES {% else %} NO {% endif %}").render(), is(" YES "));
    }

    /*
     * def test_if_boolean
     *   assert_template_result(' YES ','{% if var %} YES {% endif %}', 'var' => true)
     * end
     */
    @Test
    public void if_booleanTest() throws RecognitionException {

        assertThat(TemplateParser.DEFAULT.parse("{% if var %} YES {% endif %}").render("{ \"var\":true }"), is(" YES "));
    }

    /*
     * def test_if_or
     *   assert_template_result(' YES ','{% if a or b %} YES {% endif %}', 'a' => true, 'b' => true)
     *   assert_template_result(' YES ','{% if a or b %} YES {% endif %}', 'a' => true, 'b' => false)
     *   assert_template_result(' YES ','{% if a or b %} YES {% endif %}', 'a' => false, 'b' => true)
     *   assert_template_result('',     '{% if a or b %} YES {% endif %}', 'a' => false, 'b' => false)
     *
     *   assert_template_result(' YES ','{% if a or b or c %} YES {% endif %}', 'a' => false, 'b' => false, 'c' => true)
     *   assert_template_result('',     '{% if a or b or c %} YES {% endif %}', 'a' => false, 'b' => false, 'c' => false)
     * end
     */
    @Test
    public void if_orTest() throws RecognitionException {

        assertThat(TemplateParser.DEFAULT.parse("{% if a or b %} YES {% endif %}").render("{ \"a\":true, \"b\":true }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if a or b %} YES {% endif %}").render("{ \"a\":true, \"b\":false }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if a or b %} YES {% endif %}").render("{ \"a\":false, \"b\":true }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if a or b %} YES {% endif %}").render("{ \"a\":false, \"b\":false }"), is(""));

        assertThat(TemplateParser.DEFAULT.parse("{% if a or b or c %} YES {% endif %}").render("{ \"a\":false, \"b\":false, \"c\":true }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if a or b or c %} YES {% endif %}").render("{ \"a\":false, \"b\":false, \"c\":false }"), is(""));
    }

    /*
     * def test_if_or_with_operators
     *   assert_template_result(' YES ','{% if a == true or b == true %} YES {% endif %}', 'a' => true, 'b' => true)
     *   assert_template_result(' YES ','{% if a == true or b == false %} YES {% endif %}', 'a' => true, 'b' => true)
     *   assert_template_result('','{% if a == false or b == false %} YES {% endif %}', 'a' => true, 'b' => true)
     * end
     */
    @Test
    public void if_or_with_operatorsTest() throws RecognitionException {

        assertThat(TemplateParser.DEFAULT.parse("{% if a == true or b == true %} YES {% endif %}").render("{ \"a\":true, \"b\":true }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if a == true or b == false %} YES {% endif %}").render("{ \"a\":true, \"b\":true }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if a == false or b == false %} YES {% endif %}").render("{ \"a\":true, \"b\":true }"), is(""));
    }

    /*
     * def test_comparison_of_strings_containing_and_or_or
     *   assert_nothing_raised do
     *     awful_markup = "a == 'and' and b == 'or' and c == 'foo and bar' and d == 'bar or baz' and e == 'foo' and foo and bar"
     *     assigns = {'a' => 'and', 'b' => 'or', 'c' => 'foo and bar', 'd' => 'bar or baz', 'e' => 'foo', 'foo' => true, 'bar' => true}
     *     assert_template_result(' YES ',"{% if #{awful_markup} %} YES {% endif %}", assigns)
     *   end
     * end
     */
    @Test
    public void comparison_of_strings_containing_and_or_orTest() throws RecognitionException {

        String awfulMarkup = "a == 'and' and b == 'or' and c == 'foo and bar' and d == 'bar or baz' and e == 'foo' and foo and bar";

        String assigns = "{'a' => 'and', 'b' => 'or', 'c' => 'foo and bar', 'd' => 'bar or baz', 'e' => 'foo', 'foo' => true, 'bar' => true}"
                .replace("'", "\"")
                .replace("=>", ":");

        assertThat(TemplateParser.DEFAULT.parse("{% if " + awfulMarkup + " %} YES {% endif %}").render(assigns), is(" YES "));
    }

    /*
     * def test_comparison_of_expressions_starting_with_and_or_or
     *   assigns = {'order' => {'items_count' => 0}, 'android' => {'name' => 'Roy'}}
     *   assert_nothing_raised do
     *     assert_template_result( "YES",
     *                             "{% if android.name == 'Roy' %}YES{% endif %}",
     *                             assigns)
     *   end
     *   assert_nothing_raised do
     *     assert_template_result( "YES",
     *                             "{% if order.items_count == 0 %}YES{% endif %}",
     *                             assigns)
     *   end
     * end
     */
    @Test
    public void comparison_of_expressions_starting_with_and_or_orTest() throws RecognitionException {

        String assigns = "{'order' => {'items_count' => 0}, 'android' => {'name' => 'Roy'}}"
                .replace("'", "\"")
                .replace("=>", ":");

        assertThat(TemplateParser.DEFAULT.parse("{% if android.name == 'Roy' %}YES{% endif %}").render(assigns), is("YES"));
        assertThat(TemplateParser.DEFAULT.parse("{% if order.items_count == 0 %}YES{% endif %}").render(assigns), is("YES"));
    }

    /*
     * def test_if_and
     *   assert_template_result(' YES ','{% if true and true %} YES {% endif %}')
     *   assert_template_result('','{% if false and true %} YES {% endif %}')
     *   assert_template_result('','{% if false and true %} YES {% endif %}')
     * end
     */
    @Test
    public void if_andTest() throws RecognitionException {

        assertThat(TemplateParser.DEFAULT.parse("{% if true and true %} YES {% endif %}").render(), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if false and true %} YES {% endif %}").render(), is(""));
        assertThat(TemplateParser.DEFAULT.parse("{% if false and true %} YES {% endif %}").render(), is(""));
    }

    /*
     * def test_hash_miss_generates_false
     *   assert_template_result('','{% if foo.bar %} NO {% endif %}', 'foo' => {})
     * end
     */
    @Test
    public void hash_miss_generates_falseTest() throws RecognitionException {

        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} NO {% endif %}").render("{ \"foo\" : {} }"), is(""));
    }

    /*
     * def test_if_from_variable
     *   assert_template_result('','{% if var %} NO {% endif %}', 'var' => false)
     *   assert_template_result('','{% if var %} NO {% endif %}', 'var' => nil)
     *   assert_template_result('','{% if foo.bar %} NO {% endif %}', 'foo' => {'bar' => false})
     *   assert_template_result('','{% if foo.bar %} NO {% endif %}', 'foo' => {})
     *   assert_template_result('','{% if foo.bar %} NO {% endif %}', 'foo' => nil)
     *   assert_template_result('','{% if foo.bar %} NO {% endif %}', 'foo' => true)
     *
     *   assert_template_result(' YES ','{% if var %} YES {% endif %}', 'var' => "text")
     *   assert_template_result(' YES ','{% if var %} YES {% endif %}', 'var' => true)
     *   assert_template_result(' YES ','{% if var %} YES {% endif %}', 'var' => 1)
     *   assert_template_result(' YES ','{% if var %} YES {% endif %}', 'var' => {})
     *   assert_template_result(' YES ','{% if var %} YES {% endif %}', 'var' => [])
     *   assert_template_result(' YES ','{% if "foo" %} YES {% endif %}')
     *   assert_template_result(' YES ','{% if foo.bar %} YES {% endif %}', 'foo' => {'bar' => true})
     *   assert_template_result(' YES ','{% if foo.bar %} YES {% endif %}', 'foo' => {'bar' => "text"})
     *   assert_template_result(' YES ','{% if foo.bar %} YES {% endif %}', 'foo' => {'bar' => 1 })
     *   assert_template_result(' YES ','{% if foo.bar %} YES {% endif %}', 'foo' => {'bar' => {} })
     *   assert_template_result(' YES ','{% if foo.bar %} YES {% endif %}', 'foo' => {'bar' => [] })
     *
     *   assert_template_result(' YES ','{% if var %} NO {% else %} YES {% endif %}', 'var' => false)
     *   assert_template_result(' YES ','{% if var %} NO {% else %} YES {% endif %}', 'var' => nil)
     *   assert_template_result(' YES ','{% if var %} YES {% else %} NO {% endif %}', 'var' => true)
     *   assert_template_result(' YES ','{% if "foo" %} YES {% else %} NO {% endif %}', 'var' => "text")
     *
     *   assert_template_result(' YES ','{% if foo.bar %} NO {% else %} YES {% endif %}', 'foo' => {'bar' => false})
     *   assert_template_result(' YES ','{% if foo.bar %} YES {% else %} NO {% endif %}', 'foo' => {'bar' => true})
     *   assert_template_result(' YES ','{% if foo.bar %} YES {% else %} NO {% endif %}', 'foo' => {'bar' => "text"})
     *   assert_template_result(' YES ','{% if foo.bar %} NO {% else %} YES {% endif %}', 'foo' => {'notbar' => true})
     *   assert_template_result(' YES ','{% if foo.bar %} NO {% else %} YES {% endif %}', 'foo' => {})
     *   assert_template_result(' YES ','{% if foo.bar %} NO {% else %} YES {% endif %}', 'notfoo' => {'bar' => true})
     * end
     */
    @Test
    public void if_from_variableTest() throws RecognitionException {

        assertThat(TemplateParser.DEFAULT.parse("{% if var %} NO {% endif %}").render("{ \"var\" : false }"), is(""));
        assertThat(TemplateParser.DEFAULT.parse("{% if var %} NO {% endif %}").render("{ \"var\" : null }"), is(""));
        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} NO {% endif %}").render("{ \"foo\" : {\"bar\" : false} }"), is(""));
        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} NO {% endif %}").render("{ \"foo\" : {} }"), is(""));
        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} NO {% endif %}").render("{ \"foo\" : null }"), is(""));
        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} NO {% endif %}").render("{ \"foo\" : true }"), is(""));

        assertThat(TemplateParser.DEFAULT.parse("{% if var %} YES {% endif %}").render("{ \"var\" : \"text\" }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if var %} YES {% endif %}").render("{ \"var\" : true }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if var %} YES {% endif %}").render("{ \"var\" : 1 }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if var %} YES {% endif %}").render("{ \"var\" : {} }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if var %} YES {% endif %}").render("{ \"var\" : [] }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if \"foo\" %} YES {% endif %}").render(), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} YES {% endif %}").render("{ \"foo\" : {\"bar\" : true} }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} YES {% endif %}").render("{ \"foo\" : {\"bar\" : \"text\"} }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} YES {% endif %}").render("{ \"foo\" : {\"bar\" : 1 } }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} YES {% endif %}").render("{ \"foo\" : {\"bar\" : {} } }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} YES {% endif %}").render("{ \"foo\" : {\"bar\" : [] } }"), is(" YES "));

        assertThat(TemplateParser.DEFAULT.parse("{% if var %} NO {% else %} YES {% endif %}").render("{ \"var\" : false }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if var %} NO {% else %} YES {% endif %}").render("{ \"var\" : null }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if var %} YES {% else %} NO {% endif %}").render("{ \"var\" : true }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if \"foo\" %} YES {% else %} NO {% endif %}").render("{ \"var\" : \"text\" }"), is(" YES "));

        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} NO {% else %} YES {% endif %}").render("{ \"foo\" : {\"bar\" : false} }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} YES {% else %} NO {% endif %}").render("{ \"foo\" : {\"bar\" : true} }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} YES {% else %} NO {% endif %}").render("{ \"foo\" : {\"bar\" : \"text\"} }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} NO {% else %} YES {% endif %}").render("{ \"foo\" : {\"notbar\" : true} }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} NO {% else %} YES {% endif %}").render("{ \"foo\" : {} }"), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if foo.bar %} NO {% else %} YES {% endif %}").render("{ \"notfoo\" : {\"bar\" : true} }"), is(" YES "));
    }

    /*
     * def test_nested_if
     *   assert_template_result('', '{% if false %}{% if false %} NO {% endif %}{% endif %}')
     *   assert_template_result('', '{% if false %}{% if true %} NO {% endif %}{% endif %}')
     *   assert_template_result('', '{% if true %}{% if false %} NO {% endif %}{% endif %}')
     *   assert_template_result(' YES ', '{% if true %}{% if true %} YES {% endif %}{% endif %}')
     *
     *   assert_template_result(' YES ', '{% if true %}{% if true %} YES {% else %} NO {% endif %}{% else %} NO {% endif %}')
     *   assert_template_result(' YES ', '{% if true %}{% if false %} NO {% else %} YES {% endif %}{% else %} NO {% endif %}')
     *   assert_template_result(' YES ', '{% if false %}{% if true %} NO {% else %} NONO {% endif %}{% else %} YES {% endif %}')
     *
     * end
     */
    @Test
    public void nested_ifTest() throws RecognitionException {

        assertThat(TemplateParser.DEFAULT.parse("{% if false %}{% if false %} NO {% endif %}{% endif %}").render(), is(""));
        assertThat(TemplateParser.DEFAULT.parse("{% if false %}{% if true %} NO {% endif %}{% endif %}").render(), is(""));
        assertThat(TemplateParser.DEFAULT.parse("{% if true %}{% if false %} NO {% endif %}{% endif %}").render(), is(""));
        assertThat(TemplateParser.DEFAULT.parse("{% if true %}{% if true %} YES {% endif %}{% endif %}").render(), is(" YES "));

        assertThat(TemplateParser.DEFAULT.parse("{% if true %}{% if true %} YES {% else %} NO {% endif %}{% else %} NO {% endif %}").render(), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if true %}{% if false %} NO {% else %} YES {% endif %}{% else %} NO {% endif %}").render(), is(" YES "));
        assertThat(TemplateParser.DEFAULT.parse("{% if false %}{% if true %} NO {% else %} NONO {% endif %}{% else %} YES {% endif %}").render(), is(" YES "));
    }

    /*
     * def test_comparisons_on_null
     *   assert_template_result('','{% if null < 10 %} NO {% endif %}')
     *   assert_template_result('','{% if null <= 10 %} NO {% endif %}')
     *   assert_template_result('','{% if null >= 10 %} NO {% endif %}')
     *   assert_template_result('','{% if null > 10 %} NO {% endif %}')
     *
     *   assert_template_result('','{% if 10 < null %} NO {% endif %}')
     *   assert_template_result('','{% if 10 <= null %} NO {% endif %}')
     *   assert_template_result('','{% if 10 >= null %} NO {% endif %}')
     *   assert_template_result('','{% if 10 > null %} NO {% endif %}')
     * end
     */
    @Test
    public void comparisons_on_nullTest() throws RecognitionException {

        assertThat(TemplateParser.DEFAULT_JEKYLL.parse("{% if null < 10 %} NO {% endif %}").render(), is(""));
        assertThat(TemplateParser.DEFAULT_JEKYLL.parse("{% if null <= 10 %} NO {% endif %}").render(), is(""));
        assertThat(TemplateParser.DEFAULT_JEKYLL.parse("{% if null >= 10 %} NO {% endif %}").render(), is(""));
        assertThat(TemplateParser.DEFAULT_JEKYLL.parse("{% if null > 10 %} NO {% endif %}").render(), is(""));

        assertThat(TemplateParser.DEFAULT_JEKYLL.parse("{% if 10 < null %} NO {% endif %}").render(), is(""));
        assertThat(TemplateParser.DEFAULT_JEKYLL.parse("{% if 10 <= null %} NO {% endif %}").render(), is(""));
        assertThat(TemplateParser.DEFAULT_JEKYLL.parse("{% if 10 >= null %} NO {% endif %}").render(), is(""));
        assertThat(TemplateParser.DEFAULT_JEKYLL.parse("{% if 10 > null %} NO {% endif %}").render(), is(""));
    }

    /*
     * def test_else_if
     *   assert_template_result('0','{% if 0 == 0 %}0{% elsif 1 == 1%}1{% else %}2{% endif %}')
     *   assert_template_result('1','{% if 0 != 0 %}0{% elsif 1 == 1%}1{% else %}2{% endif %}')
     *   assert_template_result('2','{% if 0 != 0 %}0{% elsif 1 != 1%}1{% else %}2{% endif %}')
     *
     *   assert_template_result('elsif','{% if false %}if{% elsif true %}elsif{% endif %}')
     * end
     */
    @Test
    public void else_ifTest() throws RecognitionException {

        assertThat(TemplateParser.DEFAULT.parse("{% if 0 == 0 %}0{% elsif 1 == 1%}1{% else %}2{% endif %}").render(), is("0"));
        assertThat(TemplateParser.DEFAULT.parse("{% if 0 != 0 %}0{% elsif 1 == 1%}1{% else %}2{% endif %}").render(), is("1"));
        assertThat(TemplateParser.DEFAULT.parse("{% if 0 != 0 %}0{% elsif 1 != 1%}1{% else %}2{% endif %}").render(), is("2"));

        assertThat(TemplateParser.DEFAULT.parse("{% if false %}if{% elsif true %}elsif{% endif %}").render(), is("elsif"));
    }

    /*
     * def test_syntax_error_no_variable
     *   assert_raise(SyntaxError){ assert_template_result('', '{% if jerry == 1 %}')}
     * end
     */
    @Test(expected=LiquidException.class)
    public void syntax_error_no_variableTest() throws RecognitionException {
        TemplateParser.DEFAULT.parse("{% if jerry == 1 %}").render();
    }

    /*
     * def test_syntax_error_no_expression
     *   assert_raise(SyntaxError) { assert_template_result('', '{% if %}') }
     * end
     */
    @Test(expected=LiquidException.class)
    public void syntax_error_no_expressionTest() throws RecognitionException {

        TemplateParser.DEFAULT.parse("{% if %}").render();
    }

    /*
     * def test_if_with_custom_condition
     *   Condition.operators['contains'] = :[]
     *
     *   assert_template_result('yes', %({% if 'bob' contains 'o' %}yes{% endif %}))
     *   assert_template_result('no', %({% if 'bob' contains 'f' %}yes{% else %}no{% endif %}))
     * ensure
     *   Condition.operators.delete 'contains'
     * end
     */
    @Test
    public void if_with_custom_conditionTest() throws RecognitionException {

        // TODO
    }

    /*
     * def test_operators_are_ignored_unless_isolated
     *   Condition.operators['contains'] = :[]
     *
     *   assert_template_result('yes',
     *                          %({% if 'gnomeslab-and-or-liquid' contains 'gnomeslab-and-or-liquid' %}yes{% endif %}))
     * end
     */
    @Test
    public void operators_are_ignored_unless_isolatedTest() throws RecognitionException {

        // TODO
    }

    /*
     * In tags with more than one and or or operator, operators are checked in order from right to left.
     *
     * {% if true or false and false %}
     *   This evaluates to true, since the 'and' condition is checked first.
     * {% endif %}
     *
     * {% if true and false and false or true %}
     *   This evaluates to false, since the tags are checked like this:
     *
     *   true and (false and (false or true))
     *   true and (false and true)
     *   true and false
     *   false
     * {% endif %}
     */
    @Test
    public void and_or_evaluation_orderTest() throws RecognitionException {
        assertThat(TemplateParser.DEFAULT.parse("{% if true or false and false %}TRUE{% else %}FALSE{% endif %}").render(), is("TRUE"));
        assertThat(TemplateParser.DEFAULT.parse("{% if true and false and false or true %}TRUE{% else %}FALSE{% endif %}").render(), is("FALSE"));
    }

    @Test
    public void strictVariablesTest() throws RecognitionException {
        assertThat(new TemplateParser.Builder().withStrictVariables(StrictVariablesMode.STRICT).build()
            .parse("{% if obj.var %}true{% else %}false{% endif %}").render(Collections.singletonMap(
                "obj", Collections.singletonMap("var", "val"))), is("true"));

        assertThrows(VariableNotExistException.class, () -> {
            new TemplateParser.Builder().withStrictVariables(StrictVariablesMode.STRICT).build().parse(
                "{% if obj.missing %}true{% else %}false{% endif %}").render(Collections.singletonMap(
                    "obj", Collections.singletonMap("var", "val")));
        });

        assertThat(new TemplateParser.Builder().withStrictVariables(StrictVariablesMode.SANE).build()
            .parse("{% if obj.missing %}true{% else %}false{% endif %}").render(Collections.singletonMap(
                "obj", Collections.singletonMap("var", "val"))), is("false"));

        assertThrows(VariableNotExistException.class, () -> {
            new TemplateParser.Builder().withStrictVariables(StrictVariablesMode.SANE).build().parse(
                "{% if obj.nested.missing %}true{% else %}false{% endif %}").render(Collections
                    .singletonMap("obj", Collections.singletonMap("var", "val")));
        });

        assertThat(new TemplateParser.Builder().withStrictVariables(StrictVariablesMode.OFF).build()
            .parse("{% if obj.missing %}true{% else %}false{% endif %}").render(Collections.singletonMap(
                "obj", Collections.singletonMap("var", "val"))), is("false"));

        assertThat(new TemplateParser.Builder().withStrictVariables(StrictVariablesMode.OFF).build()
            .parse("{% if obj.nested.missing %}true{% else %}false{% endif %}").render(Collections
                .singletonMap("obj", Collections.singletonMap("var", "val"))), is("false"));
    }
}
