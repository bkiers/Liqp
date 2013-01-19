package liqp;

import liqp.filters.Filter;
import liqp.parser.LiquidLexer;
import liqp.parser.LiquidParser;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Used for debugging: prints the AST of some Liquid-input.
 */
public class Examples {

    private static void walk(CommonTree tree, String[] tokenNames, int indent) {

        if (tree == null) {
            return;
        }

        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
        }

        boolean leaf = tree.getChildCount() == 0;

        if (tree.getType() == LiquidLexer.EOF) {
            return;
        }

        System.out.println(tokenNames[tree.getType()] + (leaf ? "='" + tree.getText() + "'" : ""));

        for (int i = 0; i < tree.getChildCount(); i++) {
            walk((CommonTree) tree.getChild(i), tokenNames, indent + 1);
        }
    }

    public static void main(String[] args) throws Exception {
        ///*
        String test = "{% for item in array %}{{ item }}{% endfor %}";
        LiquidLexer lexer = new LiquidLexer(new ANTLRStringStream(test));
        LiquidParser parser = new LiquidParser(new CommonTokenStream(lexer));
        CommonTree ast = (CommonTree) parser.parse().getTree();
        walk(ast, parser.getTokenNames(), 0);
        //*/

        /*
        Template template = Template.parse(
                "{% if user.name == 'tobi' %}\n" +
                        "  Hello tobi\n" +
                        "{% elsif user.name == 'bob' %}\n" +
                        "  Hello bob\n" +
                        "{% else %}\n" +
                        "  Hello ???\n" +
                        "{% endif %}");


        String json = "{\"user\" : {\"name\" : \"tobi\"} }";

        Object output = template.render(json);

        System.out.printf(">>>%s<<<", output);
        */

        /*
        String source =
                "<ul id=\"products\">" +
                "  {% for p in products %}\n" +
                "  <li>\n" +
                "    {{ p.name }} costs ${{ p.price }},-: {{ p.description | downcase }}\n" +
                "  </li>" +
                "  {% endfor %}\n" +
                "</ul>\n";

        Template template = Template.parse(source);

        String variables =
                "{\"products\":[" +
                "    {\"name\":\"A\", \"price\":1, \"description\":\"BLA\"}," +
                "    {\"name\":\"B\", \"price\":2, \"description\":\"some more text\"}," +
                "    {\"name\":\"C\", \"price\":3, \"description\":\"and the LAST one\"}" +
                "  ]" +
                "}";

        String rendered = template.render(variables);

        System.out.println(rendered);
//        */
/*
        String source = "hi {{name}}";

        Template template = Template.parse(source);

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("name", "tobi");

        String rendered = template.render(variables);

        System.out.println(rendered);
*/
/*
        Filter.registerFilter(new Filter("b") {
            @Override
            public Object apply(Object value, Object... params) {
                return "<strong>" + super.asString(value) + "</strong>";
            }
        });

        String source = "hi {{ name | b: 1, 2, 3 }}";

        Template template = Template.parse(source);

        String rendered = template.render("{\"name\":\"tobi\"}");

        System.out.println(rendered);
//*/
    }
}
