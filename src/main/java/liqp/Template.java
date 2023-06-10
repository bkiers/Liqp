package liqp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;

import liqp.exceptions.LiquidException;
import liqp.filters.Filters;
import liqp.nodes.LNode;
import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;
import liqp.parser.v4.NodeVisitor;
import liqp.spi.BasicTypesSupport;
import liqp.spi.SPIHelper;
import liqp.tags.Include;
import liquid.parser.v4.LiquidLexer;
import liquid.parser.v4.LiquidParser;

/**
 * The main class of this library. Use one of its static <code>parse(...)</code> to get a hold of a
 * reference.
 * <p/>
 * Also see: https://github.com/Shopify/liquid
 */
public class Template {
    static {
        SPIHelper.applyCustomDateTypes();
    }

    /**
     * The root of the parse tree denoting the Liquid input source.
     */
    private final ParseTree root;

    /**
     * This instance's insertions.
     */
    private final Insertions insertions;

    /**
     * This instance's filters.
     */
    private final Filters filters;

    private final long templateSize;

    private TemplateContext templateContext = null;

    private ContextHolder contextHolder;

    private TemplateParser templateParser = null;

    /**
     * Creates a new Template instance from a given input.
     * 
     * @param input
     *            the file holding the Liquid source.
     * @param insertions
     *            the insertions this instance will make use of.
     * @param filters
     *            the filters this instance will make use of.
     */
    private Template(String input, Insertions insertions, Filters filters, ParseSettings parseSettings) {
        this(CharStreams.fromString(input, input), insertions, filters, parseSettings);
    }

    private Template(CharStream stream, Insertions insertions, Filters filters, ParseSettings parseSettingsIn) {
        this.templateParser = new TemplateParser.Builder()
                .withParseSettings(new ParseSettings.Builder(parseSettingsIn)
                        .withInsertions(insertions.values())
                        .with(filters.values())
                        .build())

                .build();
        this.insertions = this.templateParser.getParseSettings().insertions;
        this.filters = this.templateParser.getParseSettings().filters;

        Set<String> blockNames = this.insertions.getBlockNames();
        Set<String> tagNames = this.insertions.getTagNames();

        this.templateSize = stream.size();
        LiquidLexer lexer = new LiquidLexer(stream, this.templateParser.getParseSettings().stripSpacesAroundTags,
                this.templateParser.getParseSettings().stripSingleLine, blockNames, tagNames);
        try {
            root = parse(lexer);
        } catch (LiquidException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("could not parse input: " + stream.getSourceName(), e);
        }
    }

    /**
     * Creates a new Template instance from a given file.
     *
     * @param file
     *            the file holding the Liquid source.
     */
    private Template(File file, Insertions insertions, Filters filters, ParseSettings parseSettings)
            throws IOException {
        this(fromFile(file), insertions, filters, parseSettings);
    }

    // TemplateParser constructor
    Template(TemplateParser parser, CharStream input) {
        this(input, parser.getParseSettings().flavor.getInsertions(), parser.getParseSettings().filters, parser.getParseSettings());
        this.templateParser = parser;
    }

    private static CharStream fromStream(InputStream in) {
        try {
            return CharStreams.fromStream(in);
        } catch (IOException e) {
            throw new RuntimeException("could not parse input: " + in, e);
        }
    }

    private static CharStream fromFile(File path) {
        try {
            return CharStreams.fromFileName(path.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("could not parse input: " + path, e);
        }
    }

    private ParseTree parse(LiquidLexer lexer) {

        lexer.removeErrorListeners();

        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                    int charPositionInLine, String msg, RecognitionException e) {
                throw new LiquidException(String.format("lexer error \"%s\" on line %s, index %s", msg,
                        line, charPositionInLine), line, charPositionInLine, e);
            }
        });

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ParseSettings parseSettings = this.templateParser.getParseSettings();
        LiquidParser parser = new LiquidParser(tokens, parseSettings.flavor.isLiquidStyleInclude(), parseSettings.evaluateInOutputTag, parseSettings.errorMode);

        parser.removeErrorListeners();

        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                    int charPositionInLine, String msg, RecognitionException e) {
                throw new LiquidException(String.format("parser error \"%s\" on line %s, index %s", msg,
                        line, charPositionInLine), line, charPositionInLine, e);
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
     * Sometimes the custom insertions needs to return some extra-data, that is not rendarable. Best way
     * to allow this and keeping existing simplicity(when the result is a string) is: provide holder with
     * container for that data. Best container is current templateContext, and it is set into this holder
     * during creation.
     */
    public static class ContextHolder {
        private TemplateContext context;

        private void setContext(TemplateContext context) {
            this.context = context;
        }

        public TemplateContext getContext() {
            return context;
        }
    }

    public Template withContextHolder(ContextHolder holder) {
        this.contextHolder = holder;
        return this;
    }

    public List<Exception> errors() {
        return this.templateContext == null ? new ArrayList<>() : this.templateContext
                .errors();
    }

    /**
     * Renders the template.
     *
     * @param jsonMap
     *            a JSON-map denoting the (possibly nested) variables that can be used in this Template.
     *
     * @return a string denoting the rendered template.
     */
    public String render(String jsonMap) {
        return renderToObject(jsonMap).toString();
    }

    @SuppressWarnings("unchecked")
    public Object renderToObject(String jsonMap) {
        Map<String, Object> map;

        try {
            map = this.templateParser.getParseSettings().mapper.readValue(jsonMap, HashMap.class);
        } catch (Exception e) {
            throw new RuntimeException("invalid json map: '" + jsonMap + "'", e);
        }

        return renderToObject(map);
    }

    public String render() {
        return renderToObject().toString();
    }

    public Object renderToObject() {
        return renderToObject(new HashMap<String, Object>());
    }

    public String render(Inspectable object) {
        return renderToObject(object).toString();
    }

    public Object renderToObject(Inspectable object) {
        return renderObjectToObject(object);
    }

    /**
     * Render the template with given object, treating it same way as {@link Inspectable} instance.
     */
    public String renderObject(Object obj) {
        return renderObjectToObject(obj).toString();
    }

    private Object renderObjectToObject(Object obj) {
        LiquidSupport evaluated = getTemplateParser().getRenderSettings().evaluate(this.getTemplateParser().getParseSettings().mapper, obj);
        Map<String, Object> map = evaluated.toLiquid();
        return renderToObject(map);
    }

    /**
     * Renders the template.
     *
     * @param key
     *            a key
     * @param value
     *            the value belonging to the key
     * @param keyValues
     *            an array denoting key-value pairs where the uneven numbers (even indexes) should be
     *            Strings. If the length of this array is uneven, the last key (without the value) gets
     *            `null` attached to it. Note that a call to this method with a single String as
     *            parameter, will be handled by `render(String jsonMap)` instead.
     *
     * @return a string denoting the rendered template.
     */
    public String render(String key, Object value, Object... keyValues) {
        return renderToObject(key, value, keyValues).toString();
    }

    private Object renderToObject(String key, Object value, Object... keyValues) {
        return renderToObject(false, key, value, keyValues);
    }

    public String render(boolean convertValueToMap, String key, Object value, Object... keyValues) {
        return renderToObject(convertValueToMap, key, value, keyValues).toString();
    }

    private Object renderToObject(boolean convertValueToMap, String key, Object value, Object... keyValues) {
        Map<String, Object> map = new HashMap<String, Object>();
        putStringKey(convertValueToMap, key, value, map);

        for (int i = 0; i < keyValues.length - 1; i += 2) {
            key = String.valueOf(keyValues[i]);
            value = keyValues[i + 1];
            putStringKey(convertValueToMap, key, value, map);
        }

        return renderToObject(map);
    }

    /**
     * Renders the template.
     *
     * @param variables
     *            a Map denoting the (possibly nested) variables that can be used in this Template.
     *
     * @return a string denoting the rendered template.
     */
    public String render(final Map<String, Object> variables) {
        return renderToObject(variables).toString();
    }

    /**
     * Renders the template. The returned type is unspecified (as it may be controlled by a custom
     * {@link RenderTransformer}), however calling {@link #toString()} on that object is guaranteed to be
     * equivalent to calling {@link #render(Map)}.
     *
     * @param variables
     *            a Map denoting the (possibly nested) variables that can be used in this Template.
     *
     * @return an object denoting the rendered template.
     */
    public Object renderToObject(final Map<String, Object> variables) {
        if (this.templateParser.getProtectionSettings().isRenderTimeLimited()) {
            return renderToObject(variables, Executors.newSingleThreadExecutor(), true);
        } else {
            if (this.templateSize > this.templateParser.getProtectionSettings().maxTemplateSizeBytes) {
                throw new RuntimeException("template exceeds " +
                        this.templateParser.getProtectionSettings().maxTemplateSizeBytes + " bytes");
            }
            return renderToObjectUnguarded(variables);
        }
    }

    public String render(final Map<String, Object> variables, ExecutorService executorService,
            boolean shutdown) {
        return renderToObject(variables, executorService, shutdown).toString();
    }

    private Object renderToObject(final Map<String, Object> variables, ExecutorService executorService,
            boolean shutdown) {
        if (this.templateSize > this.templateParser.getProtectionSettings().maxTemplateSizeBytes) {
            throw new RuntimeException("template exceeds " +
                    this.templateParser.getProtectionSettings().maxTemplateSizeBytes + " bytes");
        }

        try {
            Future<Object> future = executorService.submit(() -> renderToObjectUnguarded(variables));
            return future.get(this.templateParser.getProtectionSettings().maxRenderTimeMillis, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException("exceeded the max amount of time (" + this.templateParser
                    .getProtectionSettings().maxRenderTimeMillis + " ms.)");
        } catch (Throwable t) {
            throw new RuntimeException("Oops, something unexpected happened: ", t);
        } finally {
            if (shutdown) {
                executorService.shutdown();
            }
        }
    }

    /**
     * Renders the template without guards provided by protection settings. This method has about 300x
     * times better performance than plain render.
     *
     * @param variables
     *            a Map denoting the (possibly nested) variables that can be used in this Template.
     *
     * @return a string denoting the rendered template.
     */
    public String renderUnguarded(Map<String, Object> variables) {
        return renderToObjectUnguarded(variables).toString();
    }

    /**
     * Renders the template without guards provided by protection settings.
     * 
     * The returned type is unspecified (as it may be controlled by a custom {@link RenderTransformer}),
     * however calling {@link #toString()} on that object is guaranteed to be equivalent to calling
     * {@link #renderToObjectUnguarded(Map)}.
     *
     * @param variables
     *            a Map denoting the (possibly nested) variables that can be used in this Template.
     *
     * @return an object denoting the rendered template.
     */
    public Object renderToObjectUnguarded(Map<String, Object> variables) {
        return renderToObjectUnguarded(variables, null, true);
    }

    public String renderUnguarded(Map<String, Object> variables, TemplateContext parent,
            boolean doClearThreadLocal) {
        return renderToObjectUnguarded(variables, parent, doClearThreadLocal).toString();
    }

    private TemplateContext newRootContext(Map<String, Object> variables) {
        TemplateContext context = new TemplateContext(templateParser, variables);
        Consumer<Map<String, Object>> configurator = context.getRenderSettings().getEnvironmentMapConfigurator();
        if (configurator != null) {
            configurator.accept(context.getEnvironmentMap());
        }

        return context;
    }

    public Object renderToObjectUnguarded(Map<String, Object> variables, TemplateContext parent,
            boolean doClearThreadLocal) {
        if (doClearThreadLocal) {
            BasicTypesSupport.clearReferences();
        }
        if (variables.containsKey(Include.INCLUDES_DIRECTORY_KEY)) {
            Object includeDirectory = variables.get(Include.INCLUDES_DIRECTORY_KEY);
            if (includeDirectory instanceof File) {
                variables.put(Include.INCLUDES_DIRECTORY_KEY, ((File) includeDirectory)
                        .getAbsolutePath());
            } else if (includeDirectory instanceof Path) {
                variables.put(Include.INCLUDES_DIRECTORY_KEY, ((Path) includeDirectory).toAbsolutePath()
                        .toString());
            }
        }
        ParseSettings parseSettings = templateParser.getParseSettings();
        variables = templateParser.getRenderSettings().evaluate(parseSettings.mapper, variables);

        final NodeVisitor visitor = new NodeVisitor(this.insertions, this.filters, parseSettings);
        try {
            LNode node = visitor.visit(root);
            if (parent == null) {
                this.templateContext = newRootContext(variables);
            } else {
                this.templateContext = parent.newChildContext(variables);
            }
            if (this.contextHolder != null) {
                contextHolder.setContext(templateContext);
            }
            Object rendered = node.render(this.templateContext);

            return templateContext.getParser().getRenderSettings().getRenderTransformer()
                    .transformObject(templateContext, rendered);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Renders the template using parent context
     * 
     * @param parent
     *            The parent context.
     * @return a string denoting the rendered template.
     */
    public String renderUnguarded(TemplateContext parent) {
        return renderToObjectUnguarded(parent).toString();
    }

    /**
     * Prerenders the template using parent context
     * 
     * @param parent
     *            The parent context.
     * @return a string denoting the rendered template.
     */
    private Object renderToObjectUnguarded(TemplateContext parent) {
        return renderToObjectUnguarded(new HashMap<String, Object>(), parent, true);
    }

    /**
     * Returns a string representation of the parse tree of the parsed input source.
     *
     * @return a string representation of the parse tree of the parsed input source.
     */
    public String toStringTree() {

        StringBuilder builder = new StringBuilder();

        walk(root, builder);

        return builder.toString();
    }

    /**
     * Walks a (sub) tree of the root of the input source and builds a string representation of the
     * structure of the parse tree.
     * <p/>
     * Note that line breaks and multiple white space characters are trimmed to a single white space
     * character.
     *
     * @param tree
     *            the (sub) tree.
     * @param builder
     *            the StringBuilder to fill.
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
            } else {
                tree = childStack.remove(0);

                String indent = "";

                for (int i = 0; i < childListStack.size() - 1; i++) {
                    indent += (childListStack.get(i).size() > 0) ? "|  " : "   ";
                }

                String tokenName = tree.getClass().getSimpleName().replaceAll("Context$", "");
                String tokenText = tree.getText().replaceAll("\\s+", " ");

                builder.append(indent).append(childStack.isEmpty() ? "'- " : "|- ").append(tokenName)
                        .append(tree.getChildCount() == 0 ? "='" + tokenText + "'" : "").append("\n");

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

    private void putStringKey(boolean convertValueToMap, String key, Object value,
            Map<String, Object> map) {

        if (key == null) {
            throw new RuntimeException("key cannot be null");
        }
        if (convertValueToMap && value != null) {
            if ((value.getClass().isArray() || value instanceof List) && (!(value instanceof Map))) {
                map.put(key, templateParser.getParseSettings().mapper.convertValue(value, List.class));
            } else {
                map.put(key, templateParser.getParseSettings().mapper.convertValue(value, Map.class));
            }
        } else {
            map.put(key, value);
        }
    }

    TemplateParser getTemplateParser() {
        return templateParser;
    }
}
