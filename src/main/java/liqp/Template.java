package liqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import liqp.filters.Filter;
import liqp.nodes.LNode;
import liqp.parser.Flavor;
import liqp.parser.v4.NodeVisitor;
import liqp.tags.Tag;
import liquid.parser.v4.LiquidLexer;
import liquid.parser.v4.LiquidParser;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * The main class of this library. Use one of its static
 * <code>parse(...)</code> to get a hold of a reference.
 * <p/>
 * Also see: https://github.com/Shopify/liquid
 */
public class Template {

    /**
     * The root of the parse tree denoting the Liquid input source.
     */
    private final ParseTree root;

    /**
     * This instance's tags.
     */
    private final Map<String, Tag> tags;

    /**
     * This instance's filters.
     */
    private final Map<String, Filter> filters;

    private final long templateSize;

    private ProtectionSettings protectionSettings = new ProtectionSettings.Builder().build();

    private RenderSettings renderSettings = new RenderSettings.Builder().build();

    private final ParseSettings parseSettings;

    /**
     * Creates a new Template instance from a given input.
     *  @param input
     *         the file holding the Liquid source.
     * @param tags
     *         the tags this instance will make use of.
     * @param filters
     *         the filters this instance will make use of.
     */
    private Template(String input, Map<String, Tag> tags, Map<String, Filter> filters, ParseSettings parseSettings) {

        this.tags = tags;
        this.filters = filters;
        this.parseSettings = parseSettings;

        ANTLRStringStream stream = new ANTLRStringStream(input);
        this.templateSize = stream.size();
        LiquidLexer lexer = new LiquidLexer(CharStreams.fromString(input), parseSettings.stripSpacesAroundTags);
        LiquidParser parser = createParser(lexer);

        try {
             root = parser.parse();
        }
        catch (Exception e) {
            throw new RuntimeException("could not parse input: " + input, e);
        }
    }

    /**
     * Creates a new Template instance from a given file.
     *
     * @param file
     *         the file holding the Liquid source.
     */
    private Template(File file, Map<String, Tag> tags, Map<String, Filter> filters, ParseSettings parseSettings) throws IOException {

        this.tags = tags;
        this.filters = filters;
        this.parseSettings = parseSettings;

        try {
            ANTLRFileStream stream = new ANTLRFileStream(file.getAbsolutePath());
            this.templateSize = stream.size();
            LiquidLexer lexer = new LiquidLexer(CharStreams.fromFileName(file.getAbsolutePath()), parseSettings.stripSpacesAroundTags);
            LiquidParser parser = createParser(lexer);
            root = parser.parse();
        }
        catch (Exception e) {
            throw new RuntimeException("could not parse input from " + file, e);
        }
    }

    private LiquidParser createParser(LiquidLexer lexer) {

        lexer.removeErrorListeners();

        lexer.addErrorListener(new BaseErrorListener(){
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new RuntimeException(String.format("lexer error on line %s, index %s", line, charPositionInLine), e);
            }
        });

        LiquidParser parser = new LiquidParser(new CommonTokenStream(lexer));

        parser.removeErrorListeners();

        parser.addErrorListener(new BaseErrorListener(){
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new RuntimeException(String.format("parser error on line %s, index %s", line, charPositionInLine), e);
            }
        });

        return parser;
    }

    // Use getParseTree()
    @Deprecated
    public CommonTree getAST() {
        throw new UnsupportedOperationException("The ANTLR3 CommonTree isn't available in this version of Liqp, use: getParseTree()");
    }

    /**
     * Returns the root of the parse tree of the parsed input.
     *
     * @return the root of the parse tree of the parsed input.
     */
    public ParseTree getParseTree() {
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
        return new Template(input, Tag.getTags(), Filter.getFilters(), new ParseSettings.Builder().build());
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
        return new Template(file, Tag.getTags(), Filter.getFilters(), new ParseSettings.Builder().build());
    }

    public static Template parse(File file, ParseSettings settings) throws IOException {
        return new Template(file, Tag.getTags(), Filter.getFilters(), settings);
    }

    public static Template parse(String input, ParseSettings settings) {
        return new Template(input, Tag.getTags(), Filter.getFilters(), settings);
    }

    @Deprecated // Use `parse(file, settings)` instead
    public static Template parse(File file, Flavor flavor) throws IOException {
        ParseSettings settings = new ParseSettings.Builder().withFlavor(flavor).build();
        return parse(file, settings);
    }

    @Deprecated // Use `parse(input, settings)` instead
    public static Template parse(String input, Flavor flavor) throws IOException {
        ParseSettings settings = new ParseSettings.Builder().withFlavor(flavor).build();
        return parse(input, settings);
    }

    public Template with(Tag tag) {
        this.tags.put(tag.name, tag);
        return this;
    }

    public Template with(Filter filter) {
        this.filters.put(filter.name, filter);
        return this;
    }

    public Template withProtectionSettings(ProtectionSettings protectionSettings) {
        this.protectionSettings = protectionSettings;
        return this;
    }

    public Template withRenderSettings(RenderSettings renderSettings) {
        this.renderSettings = renderSettings;
        return this;
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

        return render(map);
    }

    public String render() {
        return render(new HashMap<String, Object>());
    }

    /**
     * Renders the template.
     *
     * @param key
     *         a key
     * @param value
     *         the value belonging to the key
     * @param keyValues
     *         an array denoting key-value pairs where the
     *         uneven numbers (even indexes) should be Strings.
     *         If the length of this array is uneven, the last
     *         key (without the value) gets `null` attached to
     *         it. Note that a call to this method with a single
     *         String as parameter, will be handled by
     *         `render(String jsonMap)` instead.
     *
     * @return a string denoting the rendered template.
     */
    public String render(String key, Object value, Object... keyValues) {
        return render(false, key, value, keyValues);
    }

    public String render(boolean convertValueToMap, String key, Object value, Object... keyValues) {

        Map<String, Object> map = new HashMap<String, Object>();
        putStringKey(convertValueToMap, key, value, map);

        for (int i = 0; i < keyValues.length - 1; i += 2) {
            key = String.valueOf(keyValues[i]);
            value = keyValues[i + 1];
            putStringKey(convertValueToMap, key, value, map);
        }

        return render(map);
    }

    /**
     * Renders the template.
     *
     * @param variables
     *         a Map denoting the (possibly nested)
     *         variables that can be used in this
     *         Template.
     *
     * @return a string denoting the rendered template.
     */
    public String render(final Map<String, Object> variables) {
        return render(variables, Executors.newSingleThreadExecutor(), true);
    }

    public String render(final Map<String, Object> variables, ExecutorService executorService, boolean shutdown) {

        if (this.templateSize > this.protectionSettings.maxTemplateSizeBytes) {
            throw new RuntimeException("template exceeds " + this.protectionSettings.maxTemplateSizeBytes + " bytes");
        }

        final NodeVisitor visitor = new NodeVisitor(this.tags, this.filters, this.parseSettings);

        Callable<String> task = new Callable<String>() {
            public String call() throws Exception {
                try {
                    LNode node = visitor.visit(root);
                    Object rendered = node.render(new TemplateContext(protectionSettings, renderSettings, parseSettings.flavor, variables));
                    return rendered == null ? "" : String.valueOf(rendered);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        try {
            Future<String> future = executorService.submit(task);
            return future.get(this.protectionSettings.maxRenderTimeMillis, TimeUnit.MILLISECONDS);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("exceeded the max amount of time (" +
                    this.protectionSettings.maxRenderTimeMillis + " ms.)");
        }
        catch (Throwable t) {
            throw new RuntimeException("Oops, something unexpected happened: ", t);
        }
        finally {
            if (shutdown) {
                executorService.shutdown();
            }
        }
    }

    // Use toStringTree()
    @Deprecated
    public String toStringAST() {
        return toStringTree();
    }

    /**
     * Returns a string representation of the parse tree of the parsed
     * input source.
     *
     * @return a string representation of the parse tree of the parsed
     *         input source.
     */
    public String toStringTree() {

        StringBuilder builder = new StringBuilder();

        walk(root, builder);

        return builder.toString();
    }

    /**
     * Walks a (sub) tree of the root of the input source and builds
     * a string representation of the structure of the parse tree.
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
    private void walk(ParseTree tree, StringBuilder builder) {

        List<ParseTree> firstStack = new ArrayList<ParseTree>();
        firstStack.add(tree);

        List<List<ParseTree>> childListStack = new ArrayList<List<ParseTree>>();
        childListStack.add(firstStack);

        while (!childListStack.isEmpty()) {

            List<ParseTree> childStack = childListStack.get(childListStack.size() - 1);

            if (childStack.isEmpty()) {
                childListStack.remove(childListStack.size() - 1);
            }
            else {
                tree = childStack.remove(0);

                String indent = "";

                for (int i = 0; i < childListStack.size() - 1; i++) {
                    indent += (childListStack.get(i).size() > 0) ? "|  " : "   ";
                }

                String tokenName = tree.getClass().getSimpleName().replaceAll("Context$", "");
                String tokenText = tree.getText().replaceAll("\\s+", " ");

                builder.append(indent)
                        .append(childStack.isEmpty() ? "'- " : "|- ")
                        .append(tokenName)
                        .append(tree.getChildCount() == 0 ? "='" + tokenText + "'" : "")
                        .append("\n");

                if (tree.getChildCount() > 0) {
                    childListStack.add(new ArrayList<ParseTree>(children(tree)));
                }
            }
        }
    }

    private static List<ParseTree> children(ParseTree parent) {

        List<ParseTree> children = new ArrayList<ParseTree>();

        for (int i = 0; i < parent.getChildCount(); i++) {
            children.add(parent.getChild(i));
        }

        return children;
    }

    private void putStringKey(boolean convertValueToMap, String key, Object value, Map<String, Object> map) {

        if (key == null) {
            throw new RuntimeException("key cannot be null");
        }

        map.put(key, convertValueToMap ? parseSettings.mapper.convertValue(value, Map.class) : value);
    }
}
