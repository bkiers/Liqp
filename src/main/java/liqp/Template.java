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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;

import liqp.exceptions.LiquidException;
import liqp.filters.Filter;
import liqp.filters.Filters;
import liqp.nodes.LNode;
import liqp.parser.Flavor;
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

    @Deprecated
    protected ProtectionSettings protectionSettings = ProtectionSettings.DEFAULT;

    @Deprecated
    protected RenderSettings renderSettings = RenderSettings.DEFAULT;

    @Deprecated
    protected final ParseSettings parseSettings;

    private TemplateContext templateContext = null;

    private ContextHolder contextHolder;

    private TemplateParser templateParser = null;

    static final class BuiltTemplate extends Template {
        BuiltTemplate(TemplateParser parser, CharStream charStream) {
            super(parser, charStream);
        }

        @Deprecated
        @Override
        public Template withProtectionSettings(ProtectionSettings settings) {
            throw new UnsupportedOperationException("Already configured by " + TemplateParser.class
                    .getName());
        }

        @Deprecated
        @Override
        public Template withRenderSettings(RenderSettings settings) {
            throw new UnsupportedOperationException("Already configured by " + TemplateParser.class
                    .getName());
        }

        @Override
        public ProtectionSettings getProtectionSettings() {
            return getTemplateParser().getProtectionSettings();
        }

        @Override
        public RenderSettings getRenderSettings() {
            return getTemplateParser().getRenderSettings();
        }

        @Override
        public ParseSettings getParseSettings() {
            return getTemplateParser().getParseSettings();
        }
    }

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

    private Template(CharStream stream, Insertions insertions, Filters filters,
            ParseSettings parseSettings) {
        this.insertions = insertions.mergeWith(parseSettings.insertions);
        this.filters = filters.mergeWith(parseSettings.filters);
        this.parseSettings = parseSettings;

        Set<String> blockNames = this.insertions.getBlockNames();
        Set<String> tagNames = this.insertions.getTagNames();

        this.templateSize = stream.size();
        LiquidLexer lexer = new LiquidLexer(stream, parseSettings.stripSpacesAroundTags,
                parseSettings.stripSingleLine, blockNames, tagNames);
        try {
            root = parse(lexer);
        } catch (LiquidException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("could not parse input: " + stream.getSourceName(), e);
        }
    }

    @Deprecated
    private Template(InputStream input, Insertions insertions, Filters filters,
            ParseSettings parseSettings) {
        this(fromStream(input), insertions, filters, parseSettings);
    }

    @Deprecated
    private Template(String input, Insertions insertions, Filters filters, ParseSettings parseSettings,
            RenderSettings renderSettings) {
        this(input, insertions, filters, parseSettings);
        this.renderSettings = renderSettings;
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
        this(input, parser.getParseSettings().flavor.getInsertions(), parser.getParseSettings().flavor
                .getFilters(), parser.getParseSettings());
        this.renderSettings = parser.getRenderSettings();
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
        LiquidParser parser = new LiquidParser(tokens, this.parseSettings.flavor == Flavor.LIQUID);

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
     * Returns a new Template instance from a given input string.
     * 
     * Important: This method may have undesired side-effects from globally defined insertions and
     * filters. Please use {@link TemplateParser} instead.
     *
     * @param input
     *            the input string holding the Liquid source.
     *
     * @return a new Template instance from a given input string.
     * @deprecated use {@link TemplateParser#parse(String)}
     */
    @Deprecated
    public static Template parse(String input) {
        return new Template(input, Insertion.getCurrentInsertions(), getCurrentFilters(
                ParseSettings.DEFAULT_FLAVOR), new ParseSettings.Builder().build());
    }

    /**
     * Returns a new Template instance from a given input string with a specified set of insertions and
     * filters.
     * 
     * Important: This method may have undesired side-effects from globally defined insertions and
     * filters. Please use {@link TemplateParser} instead.
     *
     * @param input
     *            the input string holding the Liquid source.
     * @param insertions
     *            the list of insertions to use when parsing and rendering the template
     * @param filters
     *            the list of filters to use when parsing and rendering the template
     *
     * @return a new Template instance from a given input string.
     * @deprecated use {@link TemplateParser#parse(String)}
     */
    @Deprecated
    public static Template parse(String input, List<Insertion> insertions, List<Filter> filters) {
        return parse(input, insertions, filters, new ParseSettings.Builder().build(),
                RenderSettings.DEFAULT);
    }

    /**
     * Returns a new Template instance from a given input file.
     * 
     * Important: This method may have undesired side-effects from globally defined insertions and
     * filters. Please use {@link TemplateParser} instead.
     *
     * @param file
     *            the input file holding the Liquid source.
     *
     * @return a new Template instance from a given input file. * @throws IOException on error.
     * @throws IOException
     *             on error.
     * @deprecated use {@link TemplateParser#parse(File)}
     */
    @Deprecated
    public static Template parse(File file) throws IOException {
        return new Template(file, Insertion.getCurrentInsertions(), getCurrentFilters(
                ParseSettings.DEFAULT_FLAVOR), new ParseSettings.Builder().build());
    }

    /**
     * Returns a new Template instance from a given input file.
     * 
     * Important: This method may have undesired side-effects from globally defined insertions and
     * filters. Please use {@link TemplateParser} instead.
     *
     * @param file
     *            the input file holding the Liquid source.
     * @param settings
     *            the parse settings.
     * @return a new Template instance from a given input file.
     * @throws IOException
     *             on error.
     * @deprecated use {@link TemplateParser#parse(File)}
     */
    @Deprecated
    public static Template parse(File file, ParseSettings settings) throws IOException {
        return new Template(file, Insertion.getCurrentInsertions(), getCurrentFilters(settings.flavor),
                settings);
    }

    /**
     * Returns a new Template instance from a given input string.
     * 
     * Important: This method may have undesired side-effects from globally defined insertions and
     * filters. Please use {@link TemplateParser} instead.
     *
     * @param input
     *            the input string holding the Liquid source.
     * @param settings
     *            the parse settings.
     * @return a new Template instance from a given input file.
     * @deprecated use {@link TemplateParser#parse(String)}
     */
    @Deprecated
    public static Template parse(String input, ParseSettings settings) {
        return new Template(input, Insertion.getCurrentInsertions(), getCurrentFilters(settings.flavor),
                settings);
    }

    /**
     * Returns a new Template instance from a given input file.
     * 
     * Important: This method may have undesired side-effects from globally defined insertions and
     * filters. Please use {@link TemplateParser} instead.
     *
     * @param file
     *            the input file holding the Liquid source.
     * @param parseSettings
     *            the parse settings.
     * @param renderSettings
     *            the render settings.
     * @return a new Template instance from a given input file.
     * @throws IOException
     *             on error.
     * @deprecated use {@link TemplateParser#parse(File)}
     */
    @Deprecated
    public static Template parse(File file, ParseSettings parseSettings, RenderSettings renderSettings)
            throws IOException {
        TemplateParser parser = new TemplateParser.Builder().withParseSettings(
                new ParseSettings.Builder()//
                        .with(parseSettings) //
                        .withInsertions(Insertion.getCurrentInsertions().values()) //
                        .withFilters(getCurrentFilters(parseSettings.flavor).values()) //
                        .build()) //
                .withRenderSettings(renderSettings).build();

        return new Template(parser, fromFile(file));
    }

    /**
     * Returns a new Template instance from a given input string.
     * 
     * Important: This method may have undesired side-effects from globally defined insertions and
     * filters. Please use {@link TemplateParser} instead.
     *
     * @param input
     *            the input string holding the Liquid source.
     * @param parseSettings
     *            the parse settings.
     * @param renderSettings
     *            the render settings.
     * @return a new Template instance from a given input file.
     * @deprecated use {@link TemplateParser#parse(String)}
     */
    @Deprecated
    public static Template parse(String input, ParseSettings parseSettings,
            RenderSettings renderSettings) {
        return new Template(input, Insertion.getCurrentInsertions(), getCurrentFilters(
                parseSettings.flavor), parseSettings, renderSettings);
    }

    /**
     * Returns a new Template instance from a given input stream.
     * 
     * Important: This method may have undesired side-effects from globally defined insertions and
     * filters. Please use {@link TemplateParser} instead. Additionally, {@link IOException}s are
     * converted to {@link RuntimeException}.
     *
     * @param input
     *            the input stream holding the Liquid source.
     * @return a new Template instance from a given input file.
     * @deprecated use {@link TemplateParser#parse(InputStream)}
     */
    @Deprecated
    public static Template parse(InputStream input) {
        return new Template(input, Insertion.getCurrentInsertions(), getCurrentFilters(
                ParseSettings.DEFAULT_FLAVOR), new ParseSettings.Builder().build());
    }

    /**
     * Returns a new Template instance from a given input stream.
     * 
     * Important: This method may have undesired side-effects from globally defined insertions and
     * filters. Please use {@link TemplateParser} instead. Additionally, {@link IOException}s are
     * converted to {@link RuntimeException}.
     *
     * @param input
     *            the input stream holding the Liquid source.
     * @param settings
     *            the parse settings.
     * @return a new Template instance from a given input file.
     * @deprecated use {@link TemplateParser#parse(InputStream)}
     */
    @Deprecated
    public static Template parse(InputStream input, ParseSettings settings) {
        return new Template(input, Insertion.getCurrentInsertions(), getCurrentFilters(settings.flavor),
                settings);
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

    /**
     * Returns a new Template instance from a given input string.
     * 
     * Important: This method may have undesired side-effects from globally defined insertions and
     * filters. Please use {@link TemplateParser} instead.
     *
     * @param input
     *            the input string holding the Liquid source.
     * @param insertions
     *            a list of additional {@link Insertion}s
     * @param filters
     *            a list of additional {@link Filter}s
     * @param parseSettings
     *            the parse settings.
     * @param renderSettings
     *            the parse settings.
     * @return a new Template instance from a given input file.
     * @deprecated use {@link TemplateParser#parse(InputStream)}
     */
    @Deprecated
    public static Template parse(String input, List<Insertion> insertions, List<Filter> filters,
            ParseSettings parseSettings, RenderSettings renderSettings) {
        return new Template(input, Insertions.of(insertions), Filters.of(filters), parseSettings,
                renderSettings);
    }

    /**
     * Updates this template instance with the given {@link ProtectionSettings}.
     * 
     * This method will fail with an {@link UnsupportedOperationException} exception if this instance has
     * been created from a {@link TemplateParser}.
     * 
     * @param settings
     *            The protection settings.
     * @return This instance.
     * @deprecated use {@link TemplateParser}
     */
    @Deprecated
    public Template withProtectionSettings(ProtectionSettings settings) {
        this.protectionSettings = settings;
        return this;
    }

    /**
     * Updates this template instance with the given {@link RenderSettings}.
     * 
     * This method will fail with an {@link UnsupportedOperationException} exception if this instance has
     * been created from a {@link TemplateParser}.
     * 
     * @param settings
     *            The render settings.
     * @return This instance.
     * @deprecated use {@link TemplateParser}
     */
    @Deprecated
    public Template withRenderSettings(RenderSettings settings) throws UnsupportedOperationException {
        this.renderSettings = settings;
        return this;
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

    public List<RuntimeException> errors() {
        return this.templateContext == null ? new ArrayList<RuntimeException>() : this.templateContext
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
    @SuppressWarnings("unchecked")
    public String render(String jsonMap) {

        Map<String, Object> map;

        try {
            map = this.parseSettings.mapper.readValue(jsonMap, HashMap.class);
        } catch (Exception e) {
            throw new RuntimeException("invalid json map: '" + jsonMap + "'", e);
        }

        return render(map);
    }

    public String render() {
        return render(new HashMap<String, Object>());
    }

    public String render(Inspectable object) {
        return renderObject(object);
    }

    /**
     * Render the template with given object, treating it same way as {@link Inspectable} instance.
     */
    public String renderObject(Object obj) {
        LiquidSupport evaluated = renderSettings.evaluate(parseSettings.mapper, obj);
        Map<String, Object> map = evaluated.toLiquid();
        return render(map);
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
     *            a Map denoting the (possibly nested) variables that can be used in this Template.
     *
     * @return a string denoting the rendered template.
     */
    public String render(final Map<String, Object> variables) {

        if (this.getProtectionSettings().isRenderTimeLimited()) {
            return render(variables, Executors.newSingleThreadExecutor(), true);
        } else {
            if (this.templateSize > this.getProtectionSettings().maxTemplateSizeBytes) {
                throw new RuntimeException("template exceeds " +
                        this.protectionSettings.maxTemplateSizeBytes + " bytes");
            }
            return renderUnguarded(variables);
        }
    }

    public String render(final Map<String, Object> variables, ExecutorService executorService,
            boolean shutdown) {

        if (this.templateSize > this.getProtectionSettings().maxTemplateSizeBytes) {
            throw new RuntimeException("template exceeds " +
                    this.protectionSettings.maxTemplateSizeBytes + " bytes");
        }

        Callable<String> task = new Callable<String>() {
            @Override
            public String call() {
                return renderUnguarded(variables);
            }
        };

        try {
            Future<String> future = executorService.submit(task);
            return future.get(this.getProtectionSettings().maxRenderTimeMillis, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException("exceeded the max amount of time (" + this
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
        return renderUnguarded(variables, null, true);
    }

    @SuppressWarnings("deprecation")
    public String renderUnguarded(Map<String, Object> variables, TemplateContext parent,
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
        variables = renderSettings.evaluate(parseSettings.mapper, variables);

        final NodeVisitor visitor = new NodeVisitor(this.insertions, this.filters, this.parseSettings);
        try {
            LNode node = visitor.visit(root);
            if (parent == null) {
                if (templateParser == null) {
                    this.templateContext = new TemplateContext(getProtectionSettings(),
                            getRenderSettings(), getParseSettings(), variables);
                } else {
                    this.templateContext = new TemplateContext(templateParser, variables);
                }
            } else {
                this.templateContext = new TemplateContext(parent, variables);
            }
            if (this.contextHolder != null) {
                contextHolder.setContext(templateContext);
            }
            Object rendered = node.render(this.templateContext);
            return rendered == null ? "" : String.valueOf(rendered);
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
        return renderUnguarded(new HashMap<String, Object>(), parent, true);
    }

    // Use toStringTree()
    @Deprecated
    public String toStringAST() {
        return toStringTree();
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

    @SuppressWarnings("deprecation")
    private static Filters getCurrentFilters(Flavor flavor) {
        return Filter.getCurrentFilters(flavor);
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
                map.put(key, parseSettings.mapper.convertValue(value, List.class));
            } else {
                map.put(key, parseSettings.mapper.convertValue(value, Map.class));
            }
        } else {
            map.put(key, value);
        }
    }

    public ProtectionSettings getProtectionSettings() {
        return protectionSettings;
    }

    public RenderSettings getRenderSettings() {
        return renderSettings;
    }

    public ParseSettings getParseSettings() {
        return parseSettings;
    }

    TemplateParser getTemplateParser() {
        return templateParser;
    }
}
