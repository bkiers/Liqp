package liqp;

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

    public Template(String input) throws RecognitionException {
        LiquidLexer lexer = new LiquidLexer(new ANTLRStringStream(input));
        LiquidParser parser = new LiquidParser(new CommonTokenStream(lexer));
        root = (CommonTree)parser.parse().getTree();
    }

    public static Template parse(String input) throws RecognitionException {
        return new Template(input);
    }

    public Object render() {
        return render(new HashMap<String, Object>());
    }

    public Object render(Map<String, Object> variables) {

        LiquidWalker walker = new LiquidWalker(new CommonTreeNodeStream(root));

        try {
            LNode node = walker.walk();
            return node.render(variables);
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

        Map<String, Object> variables = new HashMap<String, Object>();
        Map<String, Object> user = new HashMap<String, Object>();
        user.put("name", "bob");
        variables.put("user", user);
        Object output = template.render(variables);

        //Object output = template.render();

        System.out.printf(">>>%s<<<", output);
    }
}
