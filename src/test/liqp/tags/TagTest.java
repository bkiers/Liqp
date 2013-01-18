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
}
