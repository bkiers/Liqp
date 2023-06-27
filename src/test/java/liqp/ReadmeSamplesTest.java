package liqp;

import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ReadmeSamplesTest {
    @Test
    public void testInspectable() {
        class MyParams implements Inspectable {
            @SuppressWarnings("unused")
            public String name = "tobi";
        };
        Template template = TemplateParser.DEFAULT.parse("hi {{name}}");
        String rendered = template.render(new MyParams());
        assertEquals("hi tobi", rendered);
    }
    
    @Test
    public void testLiquidSupport() {
        class MyLazy implements LiquidSupport {
            @Override
            public Map<String, Object> toLiquid() {
                return Collections.singletonMap("name", "tobi");
            }
        };
        Template template = TemplateParser.DEFAULT.parse("hi {{name}}");
        String rendered = template.render(new MyLazy());
        assertEquals("hi tobi", rendered);
    }
    
    @Test
    public void testEagerMode() {
        RenderSettings renderSettings = new RenderSettings.Builder()
                .withEvaluateMode(RenderSettings.EvaluateMode.EAGER)
                .build();

        Map<String, Object> in = Collections.singletonMap("a", new Object() {
            @SuppressWarnings("unused")
            public String val = "tobi";
        });
        
        TemplateParser parser = new TemplateParser.Builder().withRenderSettings(renderSettings).build();

        String res = parser.parse("hi {{a.val}}").render(in);
        assertEquals("hi tobi", res);
//        System.out.println(res);
    }
    
}
