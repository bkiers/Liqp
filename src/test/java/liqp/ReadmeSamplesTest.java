package liqp;

import liqp.blocks.Block;
import liqp.filters.Filter;
import liqp.nodes.LNode;
import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ReadmeSamplesTest {


    @Test
    public void testRenderTree() {
        String input =
                "<ul id=\"products\">                                       \n" +
                        "  {% for product in products %}                            \n" +
                        "    <li>                                                   \n" +
                        "      <h2>{{ product.name }}</h2>                          \n" +
                        "      Only {{ product.price | price }}                     \n" +
                        "                                                           \n" +
                        "      {{ product.description | prettyprint | paragraph }}  \n" +
                        "    </li>                                                  \n" +
                        "  {% endfor %}                                             \n" +
                        "</ul>                                                      \n";
        Template template = TemplateParser.DEFAULT.parse(input);

        ParseTree root = template.getParseTree();
    }

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

    @Test
    public void testFilterRegistration() {
        // first create template parser with new filter
        TemplateParser parser = new TemplateParser.Builder().withFilter(new Filter("b") {
            @Override
            public Object apply(Object value, TemplateContext context, Object... params) {
                // create a string from the value
                String text = super.asString(value, context);

                // replace and return *...* with <strong>...</strong>
                return text.replaceAll("\\*(\\w(.*?\\w)?)\\*", "<strong>$1</strong>");
            }
        }).build();


        // use your filter
        Template template = parser.parse("{{ wiki | b }}");
        String rendered = template.render("{\"wiki\" : \"Some *bold* text *in here*.\"}");
        System.out.println(rendered);
        /*
            Some <strong>bold</strong> text <strong>in here</strong>.
        */
    }

    @Test
    public void testFilerWithOptionalParams() {
        TemplateParser parser = new TemplateParser.Builder().withFilter(new Filter("repeat"){
            @Override
            public Object apply(Object value, TemplateContext context, Object... params) {
                // get the text of the value
                String text = super.asString(value, context);

                // check if an optional parameter is provided
                int times = params.length == 0 ? 1 : super.asNumber(params[0]).intValue();

                StringBuilder builder = new StringBuilder();

                while(times-- > 0) {
                    builder.append(text);
                }

                return builder.toString();
            }
        }).build();

        // use your filter
        Template template = parser.parse("{{ 'a' | repeat }}\n{{ 'b' | repeat:5 }}");
        String rendered = template.render();
        System.out.println(rendered);
        /*
        a
        bbbbb
        */
    }

    @Test
    public void testFilterCanBeAnything() {
        TemplateParser parser = new TemplateParser.Builder().withFilter(new Filter("sum"){
            @Override
            public Object apply(Object value, TemplateContext context, Object... params) {

                Object[] numbers = super.asArray(value, context);

                double sum = 0;

                for(Object obj : numbers) {
                    sum += super.asNumber(obj).doubleValue();
                }

                return sum;
            }
        }).build();

        Template template = parser.parse("{{ numbers | sum }}");
        String rendered = template.render("{\"numbers\" : [1, 2, 3, 4, 5]}");
        System.out.println(rendered);
        /*
            15.0
        */
    }

    @Test
    public void testBlockSample() {
        TemplateParser parser = new TemplateParser.Builder().withBlock(new Block("loop"){
            @Override
            public Object render(TemplateContext context, LNode... nodes) {
                int n = super.asNumber(nodes[0].render(context)).intValue();
                LNode block = nodes[1];
                StringBuilder builder = new StringBuilder();
                while(n-- > 0) {
                    builder.append(super.asString(block.render(context), context));
                }
                return builder.toString();
            }
        }).build();

        Template template = parser.parse("{% loop 5 %}looping!\n{% endloop %}");
        String rendered = template.render();
        System.out.println(rendered);
        /*
            looping!
            looping!
            looping!
            looping!
            looping!
        */
    }
    
}
