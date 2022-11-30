package liqp.blocks;

import liqp.Template;
import liqp.TemplateParser;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CommentTest {

    @Test
    public void renderTest() throws RecognitionException {

        String[][] tests = {
                {"{% comment %}ABC{% endcomment %}", ""},
                {"A{% comment %}B{% endcomment %}C", "AC"}
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    /*
     * def test_has_a_block_which_does_nothing
     *   assert_template_result(%|the comment block should be removed  .. right?|,
     *                          %|the comment block should be removed {%comment%} be gone.. {%endcomment%} .. right?|)
     *
     *   assert_template_result('','{%comment%}{%endcomment%}')
     *   assert_template_result('','{%comment%}{% endcomment %}')
     *   assert_template_result('','{% comment %}{%endcomment%}')
     *   assert_template_result('','{% comment %}{% endcomment %}')
     *   assert_template_result('','{%comment%}comment{%endcomment%}')
     *   assert_template_result('','{% comment %}comment{% endcomment %}')
     *
     *   assert_template_result('foobar','foo{%comment%}comment{%endcomment%}bar')
     *   assert_template_result('foobar','foo{% comment %}comment{% endcomment %}bar')
     *   assert_template_result('foobar','foo{%comment%} comment {%endcomment%}bar')
     *   assert_template_result('foobar','foo{% comment %} comment {% endcomment %}bar')
     *
     *   assert_template_result('foo  bar','foo {%comment%} {%endcomment%} bar')
     *   assert_template_result('foo  bar','foo {%comment%}comment{%endcomment%} bar')
     *   assert_template_result('foo  bar','foo {%comment%} comment {%endcomment%} bar')
     *
     *   assert_template_result('foobar','foo{%comment%}
     *                                    {%endcomment%}bar')
     * end
     */
    @Test
    public void has_a_block_which_does_nothingTest() throws RecognitionException {

        assertThat(TemplateParser.DEFAULT.parse("the comment block should be removed {%comment%} be gone.. {%endcomment%} .. right?").render(),
                is("the comment block should be removed  .. right?"));

        assertThat(TemplateParser.DEFAULT.parse("{%comment%}{%endcomment%}").render(), is(""));
        assertThat(TemplateParser.DEFAULT.parse("{%comment%}{% endcomment %}").render(), is(""));
        assertThat(TemplateParser.DEFAULT.parse("{% comment %}{%endcomment%}").render(), is(""));
        assertThat(TemplateParser.DEFAULT.parse("{% comment %}{% endcomment %}").render(), is(""));
        assertThat(TemplateParser.DEFAULT.parse("{%comment%}comment{%endcomment%}").render(), is(""));
        assertThat(TemplateParser.DEFAULT.parse("{% comment %}comment{% endcomment %}").render(), is(""));

        assertThat(TemplateParser.DEFAULT.parse("foo{%comment%}comment{%endcomment%}bar").render(), is("foobar"));
        assertThat(TemplateParser.DEFAULT.parse("foo{% comment %}comment{% endcomment %}bar").render(), is("foobar"));
        assertThat(TemplateParser.DEFAULT.parse("foo{%comment%} comment {%endcomment%}bar").render(), is("foobar"));
        assertThat(TemplateParser.DEFAULT.parse("foo{% comment %} comment {% endcomment %}bar").render(), is("foobar"));

        assertThat(TemplateParser.DEFAULT.parse("foo {%comment%} {%endcomment%} bar").render(), is("foo  bar"));
        assertThat(TemplateParser.DEFAULT.parse("foo {%comment%}comment{%endcomment%} bar").render(), is("foo  bar"));
        assertThat(TemplateParser.DEFAULT.parse("foo {%comment%} comment {%endcomment%} bar").render(), is("foo  bar"));

        assertThat(TemplateParser.DEFAULT.parse("foo{%comment%}\n         {%endcomment%}bar").render(), is("foobar"));
    }

    @Test
    public void embeddedTagInCommentTagTest() {

        // Fix for: https://github.com/bkiers/Liqp/issues/94

        String source = "{% comment %}\n" +
                "    {% if true %}\n" +
                "        hello\n" +
                "    {% endif %}\n" +
                "{% endcomment %}";

        assertThat(TemplateParser.DEFAULT.parse(source).render(), is(""));
    }
}
