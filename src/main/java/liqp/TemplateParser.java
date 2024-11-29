package liqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import liqp.antlr.LocalFSNameResolver;
import liqp.antlr.NameResolver;
import liqp.blocks.Block;
import liqp.filters.Filter;
import liqp.filters.Filters;
import liqp.filters.date.BasicDateParser;
import liqp.filters.date.Parser;
import liqp.filters.date.fuzzy.FuzzyDateParser;
import liqp.parser.Flavor;
import liqp.parser.LiquidSupport;
import liqp.tags.Tag;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;

/**
 * The new main entrance point of this library.
 */
public class TemplateParser {

    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    public static final Flavor DEFAULT_FLAVOR = Flavor.LIQP;
    /**
     * Returns a {@link TemplateParser} configured with all default settings for "Liqp" flavor.
     */
    public static final TemplateParser DEFAULT = DEFAULT_FLAVOR.defaultParser();

    public static final TemplateParser DEFAULT_JEKYLL = Flavor.JEKYLL.defaultParser();

    /**
     * Equivalent of
     * <code>
     * Liquid::Template.error_mode = :strict # Raises a SyntaxError when invalid syntax is used
     * Liquid::Template.error_mode = :warn # Adds strict errors to template.errors but continues as normal
     * Liquid::Template.error_mode = :lax # The default mode, accepts almost anything.
     * </code>
     * where usage is like:
     * <code>
     *     template = Liquid::Template.parse('', error_mode: :warn )
     * </code>
     */
    public enum ErrorMode {
        STRICT,
        WARN,
        LAX
    }

    public final Flavor flavor;
    public final boolean stripSpacesAroundTags;
    public final boolean stripSingleLine;
    public final ObjectMapper mapper;
    public final Insertions insertions;
    public final Filters filters;
    public final boolean evaluateInOutputTag;
    public final boolean strictTypedExpressions;
    public final TemplateParser.ErrorMode errorMode;
    public final boolean liquidStyleInclude;
    public final boolean liquidStyleWhere;


    /**
     * The same as <code>template.render!({}, strict_variables: true)</code> in ruby
     */
    public final boolean strictVariables;
    /**
     * This field doesn't have equivalent in ruby.
     */
    public final boolean showExceptionsFromInclude;
    public final TemplateParser.EvaluateMode evaluateMode;
    public final Locale locale;
    /**
     * Never null, if empty - system default timezone is used.
     */
    public final ZoneId defaultTimeZone;
    private final RenderTransformer renderTransformer;
    private final Consumer<Map<String, Object>> environmentMapConfigurator;
    public final NameResolver nameResolver;


    private final int limitMaxIterations;
    private final int limitMaxSizeRenderedString;
    private final long limitMaxRenderTimeMillis;
    private final long limitMaxTemplateSizeBytes;
    private final BasicDateParser dateParser;

    public enum EvaluateMode {
        LAZY,
        EAGER
    }


    /**
     * If template context is not available yet - it's ok to create new.
     * This function don't need access to local context variables,
     * as it operates with parameters.
     */
    @SuppressWarnings("hiding")
    public Map<String, Object> evaluate(final ObjectMapper mapper, Map<String, Object> variables) {
        if (evaluateMode == TemplateParser.EvaluateMode.EAGER) {
            return LiquidSupport.LiquidSupportFromInspectable.objectToMap(mapper, variables);
        }
        return variables;
    }


    /**
     * If template context is not available yet - it's ok to create new.
     * This function don't need access to local context variables,
     * as it operates with parameters.
     */
    public LiquidSupport evaluate(final Object variable) {
        return evaluate(mapper, variable);
    }

    static LiquidSupport evaluate(ObjectMapper mapper, final Object variable) {
        if (variable instanceof LiquidSupport) {
            return ((LiquidSupport) variable);
        }
        return new LiquidSupport.LiquidSupportFromInspectable(mapper, variable);
    }

    public static class Builder {

        private Flavor flavor;
        private boolean stripSpacesAroundTags = false;
        private boolean stripSingleLine = false;
        private ObjectMapper mapper;
        private List<Insertion> insertions = new ArrayList<>();
        private List<Filter> filters = new ArrayList<>();
        private Boolean evaluateInOutputTag;
        private Boolean strictTypedExpressions;
        private TemplateParser.ErrorMode errorMode;
        private Boolean liquidStyleInclude;
        private Boolean liquidStyleWhere;


        private boolean strictVariables = false;
        private boolean showExceptionsFromInclude = true;
        private EvaluateMode evaluateMode = EvaluateMode.LAZY;
        private Locale locale = DEFAULT_LOCALE;
        private ZoneId defaultTimeZone;
        private RenderTransformer renderTransformer;
        private Consumer<Map<String, Object>> environmentMapConfigurator;
        private String snippetsFolderName;


        private Integer limitMaxIterations = Integer.MAX_VALUE;
        private Integer limitMaxSizeRenderedString = Integer.MAX_VALUE;
        private Long limitMaxRenderTimeMillis  = Long.MAX_VALUE;
        private Long limitMaxTemplateSizeBytes  = Long.MAX_VALUE;
        private NameResolver nameResolver;
        private BasicDateParser dateParser;

        public Builder() {
        }

        public Builder(TemplateParser parser) {
            this.flavor = parser.flavor;
            this.stripSpacesAroundTags = parser.stripSpacesAroundTags;
            this.stripSingleLine = parser.stripSingleLine;
            this.mapper = parser.mapper;
            this.insertions = new ArrayList<>(parser.insertions.values());
            this.filters = new ArrayList<>(parser.filters.values());

            this.strictVariables = parser.strictVariables;
            this.evaluateMode = parser.evaluateMode;
            this.locale = parser.locale;
            this.renderTransformer = parser.renderTransformer;
            this.environmentMapConfigurator = parser.environmentMapConfigurator;
            this.showExceptionsFromInclude = parser.showExceptionsFromInclude;

            this.limitMaxIterations = parser.limitMaxIterations;
            this.limitMaxSizeRenderedString = parser.limitMaxSizeRenderedString;
            this.limitMaxRenderTimeMillis = parser.limitMaxRenderTimeMillis;
            this.limitMaxTemplateSizeBytes = parser.limitMaxTemplateSizeBytes;
            this.evaluateInOutputTag = parser.evaluateInOutputTag;
            this.strictTypedExpressions = parser.strictTypedExpressions;
            this.liquidStyleInclude = parser.liquidStyleInclude;
            this.liquidStyleWhere = parser.liquidStyleWhere;

            this.errorMode = parser.errorMode;
            this.nameResolver = parser.nameResolver;
            this.dateParser = parser.dateParser;
        }

        @SuppressWarnings("hiding")
        public Builder withFlavor(Flavor flavor) {
            this.flavor = flavor;
            return this;
        }

        @SuppressWarnings("hiding")
        public Builder withStripSpaceAroundTags(boolean stripSpacesAroundTags, boolean stripSingleLine) {

            if (stripSingleLine && !stripSpacesAroundTags) {
                throw new IllegalStateException(
                        "stripSpacesAroundTags must be true if stripSingleLine is true");
            }

            this.stripSpacesAroundTags = stripSpacesAroundTags;
            this.stripSingleLine = stripSingleLine;
            return this;
        }

        @SuppressWarnings("hiding")
        public Builder withStripSpaceAroundTags(boolean stripSpacesAroundTags) {
            return this.withStripSpaceAroundTags(stripSpacesAroundTags, false);
        }

        @SuppressWarnings("hiding")
        public Builder withStripSingleLine(boolean stripSingleLine) {
            this.stripSingleLine = stripSingleLine;
            return this;
        }

        @SuppressWarnings("hiding")
        public Builder withObjectMapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        public Builder withBlock(Block block) {
            this.insertions.add(block);
            return this;
        }

        public Builder withTag(Tag tag) {
            this.insertions.add(tag);
            return this;
        }

        public Builder withFilter(Filter filter) {
            this.filters.add(filter);
            return this;
        }

        @SuppressWarnings("hiding")
        public Builder withEvaluateInOutputTag(boolean evaluateInOutputTag) {
            this.evaluateInOutputTag = evaluateInOutputTag;
            return this;
        }

        public Builder withStrictTypedExpressions(boolean strictTypedExpressions) {
            this.strictTypedExpressions = strictTypedExpressions;
            return this;
        }

        @SuppressWarnings("hiding")
        public Builder withLiquidStyleInclude(boolean liquidStyleInclude) {
            this.liquidStyleInclude = liquidStyleInclude;
            return this;
        }

        @SuppressWarnings("hiding")
        public Builder withStrictVariables(boolean strictVariables) {
            this.strictVariables = strictVariables;
            return this;
        }

        @SuppressWarnings("hiding")
        public Builder withShowExceptionsFromInclude(boolean showExceptionsFromInclude) {
            this.showExceptionsFromInclude = showExceptionsFromInclude;
            return this;
        }

        @SuppressWarnings("hiding")
        public Builder withEvaluateMode(TemplateParser.EvaluateMode evaluateMode) {
            this.evaluateMode = evaluateMode;
            return this;
        }

        /**
         * Sets the {@link RenderTransformer}.
         *
         * @param renderTransformer The transformer, or {@code null} to use the default.
         * @return This builder.
         */
        @SuppressWarnings("hiding")
        public Builder withRenderTransformer(RenderTransformer renderTransformer) {
            this.renderTransformer = renderTransformer;
            return this;
        }

        @SuppressWarnings("hiding")
        public Builder withLocale(Locale locale) {
            Objects.requireNonNull(locale);
            this.locale = locale;
            return this;
        }

        /**
         * Set default timezone for showing timezone of date/time types
         * that does not have own timezone information.
         * May be null, so the timezone pattern will be omitted in formatted strings.
         * @param defaultTimeZone - value or <code>null<code/>
         * @return this builder
         */
        @SuppressWarnings("hiding")
        public Builder withDefaultTimeZone(ZoneId defaultTimeZone) {
            this.defaultTimeZone = defaultTimeZone;
            return this;
        }

        /**
         * Sets the configurator of the {@link TemplateContext}'s environment map
         * ({@link TemplateContext#getEnvironmentMap()}) instance.
         *
         * The configurator is called upon the creation of a new root context. Typically, this allows the
         * addition of certain parameters to the context environment.
         *
         * @param configurator The configurator, or {@code null}.
         * @return This builder.
         */
        public Builder withEnvironmentMapConfigurator(Consumer<Map<String, Object>> configurator) {
            this.environmentMapConfigurator = configurator;
            return this;
        }

        @SuppressWarnings("hiding")
        public Builder withSnippetsFolderName(String snippetsFolderName) {
            this.snippetsFolderName = snippetsFolderName;
            return this;
        }

        public Builder withNameResolver(NameResolver nameResolver) {
            this.nameResolver = nameResolver;
            return this;
        }

        public Builder withMaxIterations(int maxIterations) {
            this.limitMaxIterations = maxIterations;
            return this;
        }

        public Builder withMaxSizeRenderedString(int maxSizeRenderedString) {
            this.limitMaxSizeRenderedString = maxSizeRenderedString;
            return this;
        }

        public Builder withMaxRenderTimeMillis(long maxRenderTimeMillis) {
            this.limitMaxRenderTimeMillis = maxRenderTimeMillis;
            return this;
        }

        public Builder withMaxTemplateSizeBytes(long maxTemplateSizeBytes) {
            this.limitMaxTemplateSizeBytes = maxTemplateSizeBytes;
            return this;
        }

        @SuppressWarnings("hiding")
        public Builder withErrorMode(ErrorMode errorMode) {
            this.errorMode = errorMode;
            return this;
        }

        public Builder withDateParser(BasicDateParser dateParser) {
            this.dateParser = dateParser;
            return this;
        }


        @SuppressWarnings("hiding")
        public TemplateParser build() {
            Flavor fl = this.flavor;
            if (fl == null) {
                fl = DEFAULT_FLAVOR;
            }

            Boolean evaluateInOutputTag = this.evaluateInOutputTag;
            if (evaluateInOutputTag == null) {
                evaluateInOutputTag = fl.isEvaluateInOutputTag();
            }

            Boolean strictTypedExpressions = this.strictTypedExpressions;
            if (strictTypedExpressions == null) {
                strictTypedExpressions = fl.isStrictTypedExpressions();
            }

            Boolean liquidStyleInclude = this.liquidStyleInclude;
            if (liquidStyleInclude == null) {
                liquidStyleInclude = fl.isLiquidStyleInclude();
            }

            Boolean liquidStyleWhere = this.liquidStyleWhere;
            if (liquidStyleWhere == null) {
                liquidStyleWhere = fl.isLiquidStyleWhere();
            }

            ErrorMode errorMode = this.errorMode;
            if (errorMode == null) {
                errorMode = fl.getErrorMode();
            }

            if (mapper == null) {
                mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            }

            if (this.defaultTimeZone == null) {
                this.defaultTimeZone = ZoneId.systemDefault();
            }

            Insertions allInsertions = fl.getInsertions().mergeWith(Insertions.of(this.insertions));

            Filters finalFilters = fl.getFilters().mergeWith(filters);


            if (snippetsFolderName == null) {
                snippetsFolderName = fl.snippetsFolderName;
            }
            if (nameResolver == null) {
                nameResolver = new LocalFSNameResolver(snippetsFolderName);
            }

            if (dateParser == null) {
                dateParser = new FuzzyDateParser();
            }

            return new TemplateParser(strictVariables, showExceptionsFromInclude, evaluateMode, renderTransformer, locale, defaultTimeZone, environmentMapConfigurator, errorMode, fl, stripSpacesAroundTags, stripSingleLine, mapper,
                    allInsertions, finalFilters, evaluateInOutputTag, strictTypedExpressions, liquidStyleInclude, liquidStyleWhere, nameResolver, limitMaxIterations, limitMaxSizeRenderedString, limitMaxRenderTimeMillis, limitMaxTemplateSizeBytes, dateParser);
        }
    }

    /**
     * Private constructor. Use: <code>new TemplateParser.Builder().build()</code> instead.
     */
    TemplateParser(boolean strictVariables, boolean showExceptionsFromInclude, EvaluateMode evaluateMode,
                   RenderTransformer renderTransformer, Locale locale, ZoneId defaultTimeZone,
                   Consumer<Map<String, Object>> environmentMapConfigurator, ErrorMode errorMode, Flavor flavor, boolean stripSpacesAroundTags, boolean stripSingleLine,
                   ObjectMapper mapper, Insertions insertions, Filters filters, boolean evaluateInOutputTag,
                   boolean strictTypedExpressions,
                   boolean liquidStyleInclude, Boolean liquidStyleWhere, NameResolver nameResolver, int maxIterations, int maxSizeRenderedString, long maxRenderTimeMillis, long maxTemplateSizeBytes,
                   BasicDateParser dateParser) {
        this.flavor = flavor;
        this.stripSpacesAroundTags = stripSpacesAroundTags;
        this.stripSingleLine = stripSingleLine;
        this.mapper = mapper;
        this.insertions = insertions;
        this.filters = filters;
        this.evaluateInOutputTag = evaluateInOutputTag;
        this.strictTypedExpressions = strictTypedExpressions;
        this.errorMode = errorMode;
        this.liquidStyleInclude = liquidStyleInclude;
        this.liquidStyleWhere = liquidStyleWhere;

        this.strictVariables = strictVariables;
        this.showExceptionsFromInclude = showExceptionsFromInclude;
        this.evaluateMode = evaluateMode;
        this.renderTransformer = renderTransformer == null ? RenderTransformerDefaultImpl.INSTANCE
                : renderTransformer;
        this.locale = locale;
        this.defaultTimeZone = defaultTimeZone;
        this.environmentMapConfigurator = environmentMapConfigurator;
        this.nameResolver = nameResolver;

        this.limitMaxIterations = maxIterations;
        this.limitMaxSizeRenderedString = maxSizeRenderedString;
        this.limitMaxRenderTimeMillis = maxRenderTimeMillis;
        this.limitMaxTemplateSizeBytes = maxTemplateSizeBytes;
        this.dateParser = dateParser;
    }

    public Template parse(Path path) throws IOException {
        return new Template(this, CharStreams.fromPath(path), path.toAbsolutePath());
    }

    public Template parse(File file) throws IOException {
        Path path = file.toPath();
        return new Template(this, CharStreams.fromPath(path), path.toAbsolutePath());
    }

    public Template parse(String input) {
        return new Template(this, CharStreams.fromString(input), Paths.get(".").toAbsolutePath());
    }

    public Template parse(InputStream input) throws IOException {
        Path location = getLocationFromInputStream(input);
        return new Template(this, CharStreams.fromStream(input), Optional.ofNullable(location).orElseGet(() -> Paths.get(".").toAbsolutePath()));
    }

    public Template parse(Reader reader) throws IOException {
        return new Template(this, CharStreams.fromReader(reader), Paths.get(".").toAbsolutePath());
    }

    public Template parse(CharStream input) {
        Path location = NameResolver.getLocationFromCharStream(input);
        return new Template(this, input, Optional.ofNullable(location).orElseGet(() -> Paths.get(".").toAbsolutePath()));
    }


    public int getLimitMaxIterations() {
        return limitMaxIterations;
    }

    public int getLimitMaxSizeRenderedString() {
        return limitMaxSizeRenderedString;
    }

    public long getLimitMaxRenderTimeMillis() {
        return limitMaxRenderTimeMillis;
    }

    public long getLimitMaxTemplateSizeBytes() {
        return limitMaxTemplateSizeBytes;
    }

    public Boolean isRenderTimeLimited() {
        return limitMaxRenderTimeMillis != Long.MAX_VALUE;
    }

    public ErrorMode getErrorMode() {
        return errorMode;
    }

    public boolean isStripSingleLine() {
        return stripSingleLine;
    }

    public boolean isStripSpacesAroundTags() {
        return stripSpacesAroundTags;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public RenderTransformer getRenderTransformer() {
        return renderTransformer;
    }

    public Consumer<Map<String, Object>> getEnvironmentMapConfigurator() {
        return environmentMapConfigurator;
    }


    public BasicDateParser getDateParser() {
        return dateParser;
    }

    private Path getLocationFromInputStream(InputStream input) {
        try {
            if (input instanceof FileInputStream) {
                return Paths.get(((FileInputStream) input).getFD().toString()).toAbsolutePath();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
