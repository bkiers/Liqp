package liqp;

import liqp.filters.Filter;
import liqp.nodes.LNode;
import liqp.tags.Tag;
import org.antlr.runtime.tree.*;

import java.util.HashMap;
import java.util.Map;

/**
 * A class holding some examples of how to use Liqp.
 */
public class Examples {

    private static void demoPrintAST() {

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
        Template template = Template.parse(input);

        CommonTree root = template.getAST();

        System.out.println(template.toStringAST());
    }

    private static void demoSimple() {

        Template template = Template.parse("hi {{name}}");
        String rendered = template.render("name", "tobi");
        System.out.println(rendered);

        template = Template.parse("hi {{name}}");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "tobi");
        rendered = template.render(map);
        System.out.println(rendered);

        template = Template.parse("hi {{name}}");
        rendered = template.render("{\"name\" : \"tobi\"}");
        System.out.println(rendered);
    }

    private static void demoCustomStrongFilter() {

        // first register your custom filter
        Filter.registerFilter(new Filter("b"){
            @Override
            public Object apply(Object value, Object... params) {
                // create a string from the  value
                String text = super.asString(value);

                // replace and return *...* with <strong>...</strong>
                return text.replaceAll("\\*(\\w(.*?\\w)?)\\*", "<strong>$1</strong>");
            }
        });

        // use your filter
        Template template = Template.parse("{{ wiki | b }}");
        String rendered = template.render("{\"wiki\" : \"Some *bold* text *in here*.\"}");
        System.out.println(rendered);
    }

    private static void demoCustomRepeatFilter() {

        // first register your custom filter
        Filter.registerFilter(new Filter("repeat"){
            @Override
            public Object apply(Object value, Object... params) {

                // check if an optional parameter is provided
                int times = params.length == 0 ? 1 : super.asNumber(params[0]).intValue();

                // get the text of the value
                String text = super.asString(value);

                StringBuilder builder = new StringBuilder();

                while(times-- > 0) {
                    builder.append(text);
                }

                return builder.toString();
            }
        });

        // use your filter
        Template template = Template.parse("{{ 'a' | repeat }}\n{{ 'b' | repeat:5 }}");
        String rendered = template.render();
        System.out.println(rendered);
    }

    private static void demoCustomSumFilter() {

        Filter.registerFilter(new Filter("sum"){
            @Override
            public Object apply(Object value, Object... params) {

                Object[] numbers = super.asArray(value);

                double sum = 0;

                for(Object obj : numbers) {
                    sum += super.asNumber(obj).doubleValue();
                }

                return sum;
            }
        });

        Template template = Template.parse("{{ numbers | sum }}");
        String rendered = template.render("{\"numbers\" : [1, 2, 3, 4, 5]}");
        System.out.println(rendered);
    }

    private static void customLoopTag() {

        Tag.registerTag(new Tag("loop"){
            @Override
            public Object render(Context context, LNode... nodes) {

                int n = super.asNumber(nodes[0].render(context)).intValue();
                LNode block = nodes[1];

                StringBuilder builder = new StringBuilder();

                while(n-- > 0) {
                    builder.append(super.asString(block.render(context)));
                }

                return builder.toString();
            }
        });

        String source = "{% loop 5 %}looping!\n{% endloop %}";

        Template template = Template.parse(source);

        String rendered = template.render();

        System.out.println(rendered);
    }

    public static void main(String[] args) throws Exception {

        System.out.println("running liqp.Examples");

        //demoPrintAST();

        //demoSimple();

        //demoCustomStrongFilter();

        //demoCustomRepeatFilter();

        //demoCustomSumFilter();

        //customLoopTag();

        /*
            list = [{"a" => 3}, {"a" => 1}, {"a" => 2}]

            text = "list={{list | sort, 'a'}}"

            @template = Liquid::Template.parse(text)
            print ">>>" + @template.render({'list' => list}) + "<<<\n"
        */
    }
}
