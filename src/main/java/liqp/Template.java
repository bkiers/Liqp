package liqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import liqp.nodes.LNode;
import liqp.parser.LiquidLexer;
import liqp.parser.LiquidParser;
import liqp.nodes.LiquidWalker;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The main class of this library. Use one of its static
 * <code>parse(...)</code> to get a hold of a reference.
 * <p/>
 * Also see: https://github.com/Shopify/liquid
 */
public class Template {

    /**
     * The root of the AST denoting the Liquid input source.
     */
    private final CommonTree root;

    /**
     * Creates a new Template instance from a given input.
     *
     * @param input
     *         the file holding the Liquid source.
     */
    private Template(String input) {

        LiquidLexer lexer = new LiquidLexer(new ANTLRStringStream(input));
        LiquidParser parser = new LiquidParser(new CommonTokenStream(lexer));

        try {
            root = parser.parse().getTree();
        }
        catch (RecognitionException e) {
            throw new RuntimeException("could not parse input: " + input, e);
        }
    }

    /**
     * Creates a new Template instance from a given file.
     *
     * @param file
     *         the file holding the Liquid source.
     */
    private Template(File file) throws IOException {

        try {
            LiquidLexer lexer = new LiquidLexer(new ANTLRFileStream(file.getAbsolutePath()));
            LiquidParser parser = new LiquidParser(new CommonTokenStream(lexer));
            root = parser.parse().getTree();
        }
        catch (RecognitionException e) {
            throw new RuntimeException("could not parse input from " + file, e);
        }
    }

    /**
     * Returns the root of the AST of the parsed input.
     *
     * @return the root of the AST of the parsed input.
     */
    public CommonTree getAST() {
        return root;
    }

    /**
     * Returns a new Template instance from a given input string.
     *
     * @param input
     *         the input string holding the Liquid source.
     *
     * @return a new Template instance from a given input string.
     */
    public static Template parse(String input) {
        return new Template(input);
    }

    /**
     * Returns a new Template instance from a given input file.
     *
     * @param file
     *         the input file holding the Liquid source.
     *
     * @return a new Template instance from a given input file.
     */
    public static Template parse(File file) throws IOException {
        return new Template(file);
    }

    /**
     * Renders the template.
     *
     * @param jsonMap
     *         a JSON-map denoting the (possibly nested)
     *         variables that can be used in this Template.
     *
     * @return a string denoting the rendered template.
     */
    @SuppressWarnings("unchecked")
    public String render(String jsonMap) {

        Map<String, Object> map;

        try {
            map = new ObjectMapper().readValue(jsonMap, HashMap.class);
        }
        catch (Exception e) {
            throw new RuntimeException("invalid json map: '" + jsonMap + "'", e);
        }

        return render(new Context(map));
    }

    /**
     * Renders the template without a context.
     *
     * @return a string denoting the rendered template.
     */
    public String render() {
        return render(new LinkedHashMap<String, Object>());
    }

    /**
     * Renders the template with a context denoting some key-value pairs.
     *
     * @param identifier
     *         An identifier pointing to a particular value.
     * @param value
     *         The value belonging to the identifier.
     * @param keysValues
     *         An optional amount of key-value pairs.
     *
     * @return a string denoting the rendered template.
     */
    public String render(String identifier, Object value, Object... keysValues) {

        if(identifier == null) {
            throw new NullPointerException("identifier == null");
        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put(identifier, value);

        if(keysValues != null) {

            if(keysValues.length % 2 == 1) {
                throw new RuntimeException("provided a key without a value");
            }

            for(int i = 0; i < keysValues.length - 1; i += 2) {

                Object key = keysValues[i];
                Object val = keysValues[i + 1];

                if(key == null || !(key instanceof CharSequence)) {
                    throw new RuntimeException("key does not look to be a string: " + key);
                }

                map.put(String.valueOf(key), val);
            }
        }

        return render(new Context(map));
    }

    /**
     * Renders the template with a context represented as a Map.
     *
     * @param map
     *         The Map denoting the context.
     *
     * @return a string denoting the rendered template.
     */
    public String render(Map<String, Object> map) {
        return render(new Context(map));
    }

    public String render(Context context) {

        LiquidWalker walker = new LiquidWalker(new CommonTreeNodeStream(root));

        try {
            LNode node = walker.walk();
            Object rendered = node.render(context);
            return rendered == null ? "" : String.valueOf(rendered);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a string representation of the AST of the parsed
     * input source.
     *
     * @return a string representation of the AST of the parsed
     *         input source.
     */
    public String toStringAST() {

        StringBuilder builder = new StringBuilder();

        walk(root, builder);

        return builder.toString();
    }

    /**
     * Walks a (sub) tree of the root of the input source and builds
     * a string representation of the structure of the AST.
     * <p/>
     * Note that line breaks and multiple white space characters are
     * trimmed to a single white space character.
     *
     * @param tree
     *         the (sub) tree.
     * @param builder
     *         the StringBuilder to fill.
     */
    @SuppressWarnings("unchecked")
    private void walk(CommonTree tree, StringBuilder builder) {

        List<CommonTree> firstStack = new ArrayList<>();
        firstStack.add(tree);

        List<List<CommonTree>> childListStack = new ArrayList<>();
        childListStack.add(firstStack);

        while (!childListStack.isEmpty()) {

            List<CommonTree> childStack = childListStack.get(childListStack.size() - 1);

            if (childStack.isEmpty()) {
                childListStack.remove(childListStack.size() - 1);
            }
            else {
                tree = childStack.remove(0);

                String indent = "";

                for (int i = 0; i < childListStack.size() - 1; i++) {
                    indent += (childListStack.get(i).size() > 0) ? "|  " : "   ";
                }

                String tokenName = LiquidParser.tokenNames[tree.getType()];
                String tokenText = tree.getText().replaceAll("\\s+", " ").trim();

                builder.append(indent)
                        .append(childStack.isEmpty() ? "'- " : "|- ")
                        .append(tokenName)
                        .append(!tokenName.equals(tokenText) ? "='" + tokenText + "'" : "")
                        .append("\n");

                if (tree.getChildCount() > 0) {
                    childListStack.add(new ArrayList<>((List<CommonTree>) tree.getChildren()));
                }
            }
        }
    }
}
