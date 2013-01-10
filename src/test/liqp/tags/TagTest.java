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
                Double number = new Double(String.valueOf(tokens[0].render(variables)));
                return number * 2;
            }
        });

        Template template = Template.parse("{% twice 10 %}");
        String rendered = String.valueOf(template.render());

        assertThat(rendered, is("20.0"));
    }
}
