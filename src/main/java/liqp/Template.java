package liqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import liqp.exceptions.LiquidException;
import liqp.filters.Filter;
import liqp.nodes.LNode;
import liqp.parser.Flavor;
import liqp.parser.LiquidSupport;
import liqp.parser.v4.NodeVisitor;
import liqp.tags.Include;
import liqp.tags.Tag;
import liquid.parser.v4.LiquidLexer;
import liquid.parser.v4.LiquidParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    private TemplateContext templateContext = null;

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

        CharStream stream = CharStreams.fromString(input);
        this.templateSize = stream.size();
        LiquidLexer lexer = new LiquidLexer(stream, parseSettings.stripSpacesAroundTags, parseSettings.stripSingleLine);
        try {
            root = parse(lexer);
        }
        catch (LiquidException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("could not parse input: " + input, e);
        }
    }

    private Template(InputStream input, Map<String, Tag> tags, Map<String, Filter> filters, ParseSettings parseSettings) {

        this.tags = tags;
        this.filters = filters;
        this.parseSettings = parseSettings;

        try {
            CharStream stream = CharStreams.fromStream(input);
            this.templateSize = stream.size();
            LiquidLexer lexer = new LiquidLexer(stream, parseSettings.stripSpacesAroundTags, parseSettings.stripSingleLine);
            root = parse(lexer);
        }
        catch (LiquidException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("could not parse input: " + input, e);
        }
    }

    /**
     * Creates a new Template instance from a given input.
     *  @param input
     *         the file holding the Liquid source.
     * @param tags
     *         the tags this instance will make use of.
     * @param filters
     *         the filters this instance will make use of.
     * @param parseSettings
     *         the parseSettings this instance will make use of.
     * @param renderSettings
     *         the renderSettings this instance will make use of.
     */
    private Template(String input, Map<String, Tag> tags, Map<String, Filter> filters, ParseSettings parseSettings,
        RenderSettings renderSettings) {

        this(input, tags, filters, parseSettings);
        this.renderSettings = renderSettings;
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
        CharStream stream = CharStreams.fromFileName(file.getAbsolutePath());

        try {
            this.templateSize = stream.size();
            LiquidLexer lexer = new LiquidLexer(stream, parseSettings.stripSpacesAroundTags, parseSettings.stripSingleLine);
            root = parse(lexer);
        }
        catch (Exception e) {
            throw new RuntimeException("could not parse input from " + file, e);
        }
    }

    /**
     * Creates a new Template instance from a given file.
     *
     * @param file
     *         the file holding the Liquid source.
     * @param tags
     *         the tags this instance will make use of.
     * @param filters
     *         the filters this instance will make use of.
     * @param parseSettings
     *         the parseSettings this instance will make use of.
     * @param renderSettings
     *         the renderSettings this instance will make use of.
     */
    private Template(File file, Map<String, Tag> tags, Map<String, Filter> filters, ParseSettings parseSettings,
        RenderSettings renderSettings) throws IOException {

        this(file, tags, filters, parseSettings);
        this.renderSettings = renderSettings;
    }

    private ParseTree parse(LiquidLexer lexer) {

        lexer.removeErrorListeners();

        lexer.addErrorListener(new BaseErrorListener(){
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new LiquidException(String.format("lexer error \"%s\" on line %s, index %s", msg, line, charPositionInLine), line, charPositionInLine, e);
            }
        });

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LiquidParser parser = new LiquidParser(tokens);

        parser.removeErrorListeners();

        parser.addErrorListener(new BaseErrorListener(){
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new LiquidException(String.format("parser error \"%s\" on line %s, index %s", msg, line, charPositionInLine), line, charPositionInLine, e);
            }
        });

        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        try {
            return parser.parse();
        } catch (Exception e) {
            tokens.seek(0);
            parser.reset();
            parser.getInterpreter().setPredictionMode(PredictionMode.LL);
            return parser.parse();
        }
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
        return new Template(input, Tag.getTags(), Filter.getFilters(ParseSettings.DEFAULT_FLAVOR), new ParseSettings.Builder().build());
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
        return new Template(file, Tag.getTags(), Filter.getFilters(ParseSettings.DEFAULT_FLAVOR), new ParseSettings.Builder().build());
    }

    public static Template parse(File file, ParseSettings settings) throws IOException {
        return new Template(file, Tag.getTags(), Filter.getFilters(settings.flavor), settings);
    }

    public static Template parse(String input, ParseSettings settings) {
        return new Template(input, Tag.getTags(), Filter.getFilters(settings.flavor), settings);
    }

    public static Template parse(File file, ParseSettings parseSettings, RenderSettings renderSettings) throws IOException {
        return new Template(file, Tag.getTags(), Filter.getFilters(parseSettings.flavor), parseSettings, renderSettings);
    }

    public static Template parse(InputStream input) {
        return new Template(input, Tag.getTags(), Filter.getFilters(ParseSettings.DEFAULT_FLAVOR), new ParseSettings.Builder().build());
    }

    public static Template parse(InputStream input, ParseSettings settings) {
        return new Template(input, Tag.getTags(), Filter.getFilters(settings.flavor), settings);
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

    public List<RuntimeException> errors() {
        return this.templateContext == null ? new ArrayList<RuntimeException>() : this.templateContext.errors();
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

        if (this.protectionSettings.isRenderTimeLimited()) {
            return render(variables, Executors.newSingleThreadExecutor(), true);
        } else {
            if (this.templateSize > this.protectionSettings.maxTemplateSizeBytes) {
                throw new RuntimeException("template exceeds " + this.protectionSettings.maxTemplateSizeBytes + " bytes");
            }
            return renderUnguarded(variables);
        }
    }

    public String render(final Map<String, Object> variables, ExecutorService executorService, boolean shutdown) {

        if (this.templateSize > this.protectionSettings.maxTemplateSizeBytes) {
            throw new RuntimeException("template exceeds " + this.protectionSettings.maxTemplateSizeBytes + " bytes");
        }

        Callable<String> task = new Callable<String>() {
            public String call() {
                return renderUnguarded(variables);
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

    /**
     * Renders the template without guards provided by protection settings. This method has about 300x times
     * better performance than plain render.
     *
     * @param variables
     *         a Map denoting the (possibly nested)
     *         variables that can be used in this
     *         Template.
     *
     * @return a string denoting the rendered template.
     */
    public String renderUnguarded(Map<String, Object> variables) {

        if (variables.containsKey(Include.INCLUDES_DIRECTORY_KEY)) {
            Object includeDirectory = variables.get(Include.INCLUDES_DIRECTORY_KEY);
            if (includeDirectory instanceof File) {
                variables.put(Include.INCLUDES_DIRECTORY_KEY, ((File) includeDirectory).getAbsolutePath());
            }
        }
        variables = renderSettings.evaluate(parseSettings.mapper, variables);

        final NodeVisitor visitor = new NodeVisitor(this.tags, this.filters, this.parseSettings);
        try {
            LNode node = visitor.visit(root);
            this.templateContext = new TemplateContext(protectionSettings, renderSettings, parseSettings, variables);
            Object rendered = node.render(this.templateContext);
            return rendered == null ? "" : String.valueOf(rendered);
        }
        catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw e;
            } else {
                throw new RuntimeException(e);
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
        if (convertValueToMap && value != null) {
            if ((value.getClass().isArray() || value instanceof List) && (!(value instanceof Map))) {
                map.put(key, parseSettings.mapper.convertValue(value, List.class));
            } else {
                map.put(key, parseSettings.mapper.convertValue(value, Map.class));
            }
        } else {
            map.put(key, value);
        }
    }
}
