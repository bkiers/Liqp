package liqp.nodes;

import liqp.Template;
import org.junit.Test;

import static org.junit.Assert.*;

public class ContainsNodeTest {
    @Test
    public void testContainsNodeWhenNumericItemInCollection() {
        // given
        String data = "{ \"obj\" : { \"groups\" : [1, 2] } }";

        // when
        String rendered = Template.parse("{{obj.groups contains 1}}").render(data);

        // then
        assertEquals("true", rendered);
    }
}
