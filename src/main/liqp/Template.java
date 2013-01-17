package liqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import liqp.nodes.LNode;
import liqp.parser.LiquidLexer;
import liqp.parser.LiquidParser;
import liqp.parser.LiquidWalker;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The main class of this library. Use one of its static
 * <code>parse(...)</code> to get a hold of a reference.
 *
 * Also see: https://github.com/Shopify/liquid
 */
public class Template {

    /**
     * The root of the AST denoting the Liquid input source.
     */
    private final CommonTree root;

    /**
     * Creates a new Template instance from a given file.
     *
     * @param input the file holding the Liquid source.
     */
    private Template(String input) {

        LiquidLexer lexer = new LiquidLexer(new ANTLRStringStream(input));
        LiquidParser parser = new LiquidParser(new CommonTokenStream(lexer));

        try {
            root = (CommonTree)parser.parse().getTree();
        }
        catch (RecognitionException e) {
            throw new RuntimeException("could not parse input: " + input, e);
        }
    }

    /**
     * Creates a new Template instance from a given file.
     *
     * @param file the file holding the Liquid source.
     */
    private Template(File file) throws IOException {

        try {
            LiquidLexer lexer = new LiquidLexer(new ANTLRFileStream(file.getAbsolutePath()));
            LiquidParser parser = new LiquidParser(new CommonTokenStream(lexer));
            root = (CommonTree)parser.parse().getTree();
        }
        catch (RecognitionException e) {
            throw new RuntimeException("could not parse input from " + file, e);
        }
    }

    /**
     * Returns a new Template instance from a given input string.
     *
     * @param input the input string holding the Liquid source.
     * @return a new Template instance from a given input string.
     */
    public static Template parse(String input) {
        return new Template(input);
    }

    /**
     * Returns a new Template instance from a given input file.
     *
     * @param file the input file holding the Liquid source.
     * @return a new Template instance from a given input file.
     */
    public static Template parse(File file) throws IOException {
        return new Template(file);
    }

    /**
     * Renders the template.
     *
     * @return a string denoting the rendered template.
     */
    public String render() {
        return render(new HashMap<String, Object>());
    }

    /**
     * Renders the template.
     *
     * @param jsonMap a JSON-map denoting the (possibly nested)
     *                variables that can be used in this Template.
     * @return a string denoting the rendered template.
     */
    @SuppressWarnings("unchecked")
    public String render(String jsonMap) {

        Map<String, Object> variables;

        try {
            variables = new ObjectMapper().readValue(jsonMap, HashMap.class);
        }
        catch (Exception e) {
            throw new RuntimeException("invalid json map: '" + jsonMap + "'", e);
        }

        return render(variables);
    }

    /**
     * Renders the template.
     *
     * @param variables a Map denoting the (possibly nested)
     *                  variables that can be used in this
     *                  Template.
     * @return a string denoting the rendered template.
     */
    public String render(Map<String, Object> variables) {

        LiquidWalker walker = new LiquidWalker(new CommonTreeNodeStream(root));

        try {
            LNode node = walker.walk();
            return String.valueOf(node.render(variables));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
