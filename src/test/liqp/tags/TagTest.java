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

        Tag.registerTag("twice", new Tag() {
            @Override
            public Object render(Map<String, Object> variables, LNode... tokens) {
                Double number = super.asNumber(tokens[0].render(variables)).doubleValue();
                return number * 2;
            }
        });

        Template template = Template.parse("{% twice 10 %}");
        String rendered = String.valueOf(template.render());

        assertThat(rendered, is("20.0"));
    }

    @Test
    public void testCustomTagBlock() throws RecognitionException {

        Tag.registerTag("twice", new Tag() {
            @Override
            public Object render(Map<String, Object> variables, LNode... tokens) {
                LNode blockNode = tokens[tokens.length - 1];
                String blockValue = super.asString(blockNode.render(variables));
                return blockValue + " " + blockValue;
            }
        });

        Template template = Template.parse("{% twice %}abc{% endtwice %}");
        String rendered = String.valueOf(template.render());

        assertThat(rendered, is("abc abc"));
    }
}
