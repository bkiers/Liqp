package liqp.tags;

import liqp.Template;
import liqp.nodes.LNode;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TagTest {

    @Test
    public void testCustomTag() throws RecognitionException {

        Tag.registerTag(new Tag("twice") {
            @Override
            public Object render(Map<String, Object> context, LNode... nodes) {
                Double number = super.asNumber(nodes[0].render(context)).doubleValue();
                return number * 2;
            }
        });

        Template template = Template.parse("{% twice 10 %}");
        String rendered = String.valueOf(template.render());

        assertThat(rendered, is("20.0"));
    }

    @Test
    public void testCustomTagBlock() throws RecognitionException {

        Tag.registerTag(new Tag("twice") {
            @Override
            public Object render(Map<String, Object> context, LNode... nodes) {
                LNode blockNode = nodes[nodes.length - 1];
                String blockValue = super.asString(blockNode.render(context));
                return blockValue + " " + blockValue;
            }
        });

        Template template = Template.parse("{% twice %}abc{% endtwice %}");
        String rendered = String.valueOf(template.render());

        assertThat(rendered, is("abc abc"));
    }


    @Test
    public void breakTest() throws RecognitionException {

        final String context = "{\"array\":[11,22,33,44,55]}";

        final String markup = "{% for item in array %}" +
                "{% if item > 35 %}{% break %}{% endif %}" +
                "{{ item }}" +
                "{% endfor %}";

        assertThat(Template.parse(markup).render(context), is("112233"));
    }

    /*
     * def test_break_with_no_block
     *   assigns = {'i' => 1}
     *   markup = '{% break %}'
     *   expected = ''
     *
     *   assert_template_result(expected, markup, assigns)
     * end
     */
    @Test
    public void breakWithNoBlockTest() throws RecognitionException {

        assertThat(Template.parse("{% break %}").render(), is(""));
    }

    @Test
    public void continueTest() throws RecognitionException {

        final String context = "{\"array\":[11,22,33,44,55]}";

        final String markup = "{% for item in array %}" +
                "{% if item < 35 %}{% continue %}{% endif %}" +
                "{{ item }}" +
                "{% endfor %}";

        assertThat(Template.parse(markup).render(context), is("4455"));
    }

    /*
     * def test_break_with_no_block
     *   assigns = {'i' => 1}
     *   markup = '{% break %}'
     *   expected = ''
     *
     *   assert_template_result(expected, markup, assigns)
     * end
     */
    @Test
    public void continueWithNoBlockTest() throws RecognitionException {

        assertThat(Template.parse("{% continue %}").render(), is(""));
    }
}
