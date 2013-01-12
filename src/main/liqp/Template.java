package liqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import liqp.nodes.LNode;
import liqp.parser.LiquidLexer;
import liqp.parser.LiquidParser;
import liqp.parser.LiquidWalker;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import java.util.HashMap;
import java.util.Map;

public class Template {

    private final CommonTree root;

    public Template(String input) {

        LiquidLexer lexer = new LiquidLexer(new ANTLRStringStream(input));
        LiquidParser parser = new LiquidParser(new CommonTokenStream(lexer));

        try {
            root = (CommonTree)parser.parse().getTree();
        } catch (RecognitionException e) {
            throw new RuntimeException("could not parse input: " + input, e);
        }
    }

    public static Template parse(String input) {
        return new Template(input);
    }

    public String render() {
        return render(new HashMap<String, Object>());
    }

    public String render(String jsonMap) {

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> variables = new ObjectMapper().readValue(jsonMap, HashMap.class);
            return render(variables);
        }
        catch (Exception e) {
            throw new RuntimeException("invalid json map: '" + jsonMap + "'", e);
        }
    }

    public String render(Map<String, Object> variables) {

        LiquidWalker walker = new LiquidWalker(new CommonTreeNodeStream(root));

        try {
            LNode node = walker.walk();
            return String.valueOf(node.render(variables)).trim();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        Template template = Template.parse(
                "{% if user.name == 'tobi' %}\n" +
                        "  Hello tobi\n" +
                        "{% elsif user.name == 'bob' %}\n" +
                        "  Hello bob\n" +
                        "{% else %}\n" +
                        "  Hello ???\n" +
                        "{% endif %}");


        String json = "{\"user\" : {\"name\" : \"tobii\"} }";

        Object output = template.render(json);

        System.out.printf(">>>%s<<<", output);
    }
}
