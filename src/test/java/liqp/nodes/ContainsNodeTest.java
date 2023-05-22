package liqp.nodes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import liqp.TemplateParser;

public class ContainsNodeTest {
    @Test
    public void testContainsNodeWhenNumericItemInCollection() {
        // given
        String data = "{ \"obj\" : { \"groups\" : [1, 2] } }";

        // when
        String rendered = TemplateParser.DEFAULT.parse("{{obj.groups contains 1}}").render(data);

        // then
        assertEquals("true", rendered);
    }
}
