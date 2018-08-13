package liqp.nodes;

import liqp.Template;
import liqp.TemplateContext;
import liqp.tags.Tag;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

public class BlockNodeTest {

    /*
     * def test_with_custom_tag
     *   Liquid::Template.register_tag("testtag", Block)
     *
     *   assert_nothing_thrown do
     *     template = Liquid::Template.parse( "{% testtag %} {% endtesttag %}")
     *   end
     * end
     */
    @Test
    public void customTagTest() throws RecognitionException {

        Tag.registerTag(new Tag("testtag"){
            @Override
            public Object render(TemplateContext context, LNode... nodes) {
                return null;
            }
        });

        Template.parse("{% testtag %} {% endtesttag %}").render();
    }
}
