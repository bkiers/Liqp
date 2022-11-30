package liqp.tags;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import liqp.Template;
import liqp.TemplateParser;

public class IncrementTest {

    //  def test_inc
    //    assert_template_result('0', '{%increment port %}', {})
    //    assert_template_result('0 1', '{%increment port %} {%increment port%}', {})
    //    assert_template_result('0 0 1 2 1',
    //      '{%increment port %} {%increment starboard%} ' \
    //      '{%increment port %} {%increment port%} ' \
    //      '{%increment starboard %}', {})
    //  end
    @Test
    public void testInc() {

        String[][] tests = {
                {"{%increment port %}", "0"},
                {"{%increment port %} {%increment port%}", "0 1"},
                {"{%increment port %} {%increment starboard%} {%increment port %} {%increment port%} {%increment starboard %}", "0 0 1 2 1"},
                {"{% assign x = 42 %}{{x}} {%increment x %} {%increment x %} {{x}}", "42 0 1 42"},
                {"{% increment x %} {% increment x %} {{x}}", "0 1 2"},
                {"{% increment var %}{% increment var %}{{ var }}{% increment var %}", "0122"},
                {"{% increment var %}{% assign var=5 %}{% increment var %}{{ var }}{% increment var %}", "0152"},
                {"{% increment var %}{{ var }}{% assign var=5 %}{% increment var %}{{ var }}{% increment var %}", "01152"},
                {"{% increment var %}{% assign exp='var' %}{{ [exp] }}", "01"},
                {"{% increment var %}{% assign a = var | plus: 5 %}{{ a }}", "06"}
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}

