package liqp;

import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ReadmeSamplesTest {


    @Test
    public void testReadMeIntro() {
        TemplateParser parser = new TemplateParser.Builder().build();
        Template template = parser.parse("hi {{name}}");
        String rendered = template.render("name", "tobi");
        System.out.println(rendered);
    }

    @Test
    public void testReadMeMap() {
        Template template = new TemplateParser.Builder().build().parse("hi {{name}}");
        Map<String, Object> map = new HashMap<>();
        map.put("name", "tobi");
        String rendered = template.render(map);
        System.out.println(rendered);
    }

    @Test
    public void testReadMeJson() {
        Template template = new TemplateParser.Builder().build().parse("hi {{name}}");
        String rendered = template.render("{\"name\" : \"tobi\"}");
        System.out.println(rendered);
    }

    @Test
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    public void testInspectable() {
        class MyParams implements Inspectable {
            public String name = "tobi";
        };
        Template template = TemplateParser.DEFAULT.parse("hi {{name}}");
        String rendered = template.render(new MyParams());
        System.out.println(rendered);
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
        System.out.println(rendered);
        assertEquals("hi tobi", rendered);
    }
    
    @Test
    public void testEagerMode() {

        Map<String, Object> in = Collections.singletonMap("a", new Object() {
            @SuppressWarnings("unused")
            public String val = "tobi";
        });
        
        TemplateParser parser = new TemplateParser.Builder().withEvaluateMode(TemplateParser.EvaluateMode.EAGER).build();

        String res = parser.parse("hi {{a.val}}").render(in);
        assertEquals("hi tobi", res);
//        System.out.println(res);
    }
    
}
