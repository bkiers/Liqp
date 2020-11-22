package liqp.tags;

import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
                {"{% increment var %}{% assign exp='var' %}{{ [exp] }}", "01"}
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}

