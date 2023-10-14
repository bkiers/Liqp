package liqp.parser;

import liqp.Insertions;
import liqp.TemplateParser;
import liqp.filters.Filters;

public enum Flavor {
    LIQUID("snippets",
            Filters.DEFAULT_FILTERS,
            Insertions.STANDARD_INSERTIONS,
            TemplateParser.ErrorMode.STRICT,
            true,
            true,
            false,
            true
    ), //

    JEKYLL("_includes",
            Filters.JEKYLL_FILTERS,
            Insertions.STANDARD_INSERTIONS,
            TemplateParser.ErrorMode.WARN,
            false,
            false,
            false,
            true
    ),

    LIQP("snippets",
            Filters.JEKYLL_FILTERS,
            Insertions.STANDARD_INSERTIONS,
            TemplateParser.ErrorMode.STRICT,
            true,
            true,
            true,
            false
    );

    public final String snippetsFolderName;
    private final Filters filters;
    private final Insertions insertions;
    private final TemplateParser.ErrorMode errorMode;
    private final boolean liquidStyleInclude;
    private final boolean liquidStyleWhere;
    private final boolean evaluateInOutputTag;
    private final boolean strictTypedExpressions;
    private TemplateParser parser;

    Flavor(String snippetsFolderName,
           Filters filters,
           Insertions insertions,
           TemplateParser.ErrorMode errorMode,
           boolean isLiquidStyleInclude,
           boolean isLiquidStyleWhere,
           boolean evaluateInOutputTag,
           boolean strictTypedExpressions
           ) {
        this.snippetsFolderName = snippetsFolderName;
        this.filters = filters;
        this.insertions = insertions;
        this.errorMode = errorMode;
        this.liquidStyleInclude = isLiquidStyleInclude;
        this.liquidStyleWhere = isLiquidStyleWhere;
        this.evaluateInOutputTag = evaluateInOutputTag;
        this.strictTypedExpressions = strictTypedExpressions;
    }

    /**
     * Returns the common {@link Filters} of this flavor.
     * 
     * @return The {@link Filters}.
     */
    public Filters getFilters() {
        return filters;
    }

    /**
     * Returns the common {@link Insertions} of this flavor.
     * 
     * @return The {@link Insertions}.
     */
    public Insertions getInsertions() {
        return insertions;
    }

    /**
     * Returns the default {@link TemplateParser} for this Flavor.
     * 
     * @return The parser.
     */
    public TemplateParser defaultParser() {
        if (parser == null) {
            parser = new TemplateParser.Builder().withFlavor(this).build();
        }
        return parser;
    }

    /**
     * Returns the default {@link liqp.TemplateParser.ErrorMode} for this Flavor.
     */
    public TemplateParser.ErrorMode getErrorMode() {
        return errorMode;
    }

    /**
     * Return default behavior for this Flavor whenever expressions must be evaluated in output tag
     * @return if {@code true}.
     */
    public boolean isEvaluateInOutputTag() {
        return evaluateInOutputTag;
    }

    public boolean isLiquidStyleInclude() {
        return liquidStyleInclude;
    }

    public boolean isLiquidStyleWhere() {
        return liquidStyleWhere;
    }

    /**
     * ruby is strictly typed, so comparing string like '98' and number like 97 will raise exception,
     * and we must follow this behavior for compatibility in Liquid and Jekyll flavors
     * but historically in this library and practice from other implementations show that people expect weak-typing in expressions.
     * So this is a flag that actually control this.
     */
    public boolean isStrictTypedExpressions() {
        return strictTypedExpressions;
    }
}
