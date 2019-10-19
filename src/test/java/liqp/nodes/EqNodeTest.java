package liqp.nodes;

import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EqNodeTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{% if 1.0 == 1 %}TRUE{% else %}FALSE{% endif %}", "TRUE"},
                {"{% if nil == nil %}TRUE{% else %}FALSE{% endif %}", "TRUE"},
                {"{% if false == false %}TRUE{% else %}FALSE{% endif %}", "TRUE"},
                {"{% if \"\" == '' %}TRUE{% else %}FALSE{% endif %}", "TRUE"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }

    /*
     * def test_illegal_symbols
     *   assert_template_result('', '{% if true == empty %}?{% endif %}', {})
     *   assert_template_result('', '{% if true == null %}?{% endif %}', {})
     *   assert_template_result('', '{% if true == blank %}?{% endif %}', {})
     *
     *   assert_template_result('', '{% if empty == true %}?{% endif %}', {})
     *   assert_template_result('', '{% if empty == null %}?{% endif %}', {})
     *   assert_template_result('', '{% if empty == blank %}?{% endif %}', {})
     *
     *   assert_template_result('', '{% if null == true %}?{% endif %}', {})
     *   assert_template_result('', '{% if null == empty %}?{% endif %}', {})
     *   assert_template_result('', '{% if null == blank %}?{% endif %}', {})
     *
     *   assert_template_result('', '{% if blank == true %}?{% endif %}', {})
     *   assert_template_result('', '{% if blank == empty %}?{% endif %}', {})
     *   assert_template_result('', '{% if blank == null %}?{% endif %}', {})
     * end
     */
    @Test
    public void illegal_symbolsTest() throws Exception {

        assertThat(Template.parse("{% if true == empty %}?{% endif %}").render(), is(""));
        assertThat(Template.parse("{% if true == null %}?{% endif %}").render(), is(""));
        assertThat(Template.parse("{% if true == blank %}?{% endif %}").render(), is(""));

        assertThat(Template.parse("{% if empty == true %}?{% endif %}").render(), is(""));
        assertThat(Template.parse("{% if empty == null %}?{% endif %}").render(), is(""));
        assertThat(Template.parse("{% if empty == blank %}?{% endif %}").render(), is(""));

        assertThat(Template.parse("{% if null == true %}?{% endif %}").render(), is(""));
        assertThat(Template.parse("{% if null == empty %}?{% endif %}").render(), is(""));
        assertThat(Template.parse("{% if null == blank %}?{% endif %}").render(), is(""));

        assertThat(Template.parse("{% if blank == true %}?{% endif %}").render(), is(""));
        assertThat(Template.parse("{% if blank == empty %}?{% endif %}").render(), is(""));
        assertThat(Template.parse("{% if blank == null %}?{% endif %}").render(), is(""));
    }
}
