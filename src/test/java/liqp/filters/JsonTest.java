package liqp.filters;

import liqp.Template;
import liqp.TemplateParser;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class JsonTest {

    @Test
    public void testWhenStringIsInputShouldBeStringified() {
        Template template = TemplateParser.DEFAULT.parse("{{ 'Hello, World!' | json }}");
        String rendered = template.render();
        assertEquals("\"Hello, World!\"", rendered);
    }

    @Test
    public void testWhenObjectIsInputShouldBeStringified() {
        Template template = TemplateParser.DEFAULT.parse("{{ obj | json }}");
        java.util.Map<String, Object> map = new HashMap<>();
        java.util.Map<String, Object> nested = new HashMap<>();
        nested.put("key", "value");
        map.put("obj", nested);

        String rendered = template.render(map);
        assertEquals("{\"key\":\"value\"}", rendered);
    }
}