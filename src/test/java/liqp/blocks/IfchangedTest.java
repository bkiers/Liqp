package liqp.blocks;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import liqp.Template;
import liqp.TemplateParser;

public class IfchangedTest {

    //  def test_ifchanged
    //
    //    assigns = { 'array' => [ 1, 1, 2, 2, 3, 3] }
    //    assert_template_result('123', '{%for item in array%}{%ifchanged%}{{item}}{% endifchanged %}{%endfor%}', assigns)
    //
    //    assigns = { 'array' => [ 1, 1, 1, 1] }
    //    assert_template_result('1', '{%for item in array%}{%ifchanged%}{{item}}{% endifchanged %}{%endfor%}', assigns)
    //  end
    @Test
    public void renderTest() {

        String[][] tests = {
                {"{%for item in array%}{%ifchanged%}{{item}}{% endifchanged %}{%endfor%}", "{ \"array\": [1, 1] }", "1"},
                {"{%for item in array%}{%ifchanged%}{{item}}{% endifchanged %}{%endfor%}", "{ \"array\": [1, 1, 2, 2, 3, 3] }", "123"},
                {"{%for item in array%}{%ifchanged%}{{item}}{% endifchanged %}{%endfor%}", "{ \"array\": [1, 1, 1, 1] }", "1"},
                {"{%for item in array%}{%ifchanged%}{{item}}{% endifchanged %}{%endfor%}", "{ \"array\": [] }", ""},
                {"{%for item in array%}{%ifchanged%}{{item}}{% endifchanged %}{%endfor%}", "{}", ""}
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render(test[1]));

            assertThat(rendered, is(test[2]));
        }
    }

    @Test
    public void testIfChangedScope() {
        String render = TemplateParser.DEFAULT.parse("{% ifchanged %}1{% endifchanged %}{%for item in (1..4) %}{% ifchanged %}{{ item }}{% endifchanged %}{% endfor %}{% ifchanged %}4{% endifchanged %}{% ifchanged %}5{% endifchanged %}").render();
        assertThat(render, is("12345"));
    }
}
