package liqp.parser;

import liqp.Insertions;
import liqp.ParseSettings;
import liqp.TemplateParser;
import liqp.filters.Filters;

public enum Flavor {
    LIQUID("snippets",
            Filters.DEFAULT_FILTERS,
            Insertions.STANDARD_INSERTIONS,
            TemplateParser.ErrorMode.lax,
            true,
            true,
            false
    ), //

    JEKYLL("_includes",
            Filters.JEKYLL_FILTERS,
            Insertions.STANDARD_INSERTIONS,
            TemplateParser.ErrorMode.warn,
            false,
            false,
            false
    ),

    LIQP("snippets",
            Filters.JEKYLL_FILTERS,
            Insertions.STANDARD_INSERTIONS,
            TemplateParser.ErrorMode.strict,
            true,
            true,
            true
    );

    public final String snippetsFolderName;
    private final Filters filters;
    private final Insertions insertions;
    private final TemplateParser.ErrorMode errorMode;
    private final boolean liquidStyleInclude;
    private final boolean liquidStyleWhere;
    private final boolean evaluateInOutputTag;
    private ParseSettings parseSettings;
    private TemplateParser parser;

    Flavor(String snippetsFolderName,
           Filters filters,
           Insertions insertions,
           TemplateParser.ErrorMode errorMode,
           boolean isLiquidStyleInclude,
           boolean isLiquidStyleWhere,
           boolean evaluateInOutputTag) {
        this.snippetsFolderName = snippetsFolderName;
        this.filters = filters;
        this.insertions = insertions;
        this.errorMode = errorMode;
        this.liquidStyleInclude = isLiquidStyleInclude;
        this.liquidStyleWhere = isLiquidStyleWhere;
        this.evaluateInOutputTag = evaluateInOutputTag;
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
     * Returns the default {@link ParseSettings} for this Flavor.
     * 
     * @return The settings.
     */
    public ParseSettings defaultParseSettings() {
        if (parseSettings == null) {
            parseSettings = new ParseSettings.Builder().withFlavor(this).build();
        }
        return parseSettings;
    }

    /**
     * Returns the default {@link TemplateParser} for this Flavor.
     * 
     * @return The parser.
     */
    public TemplateParser defaultParser() {
        if (parser == null) {
            parser = new TemplateParser.Builder().withParseSettings(defaultParseSettings()).build();
        }
        return parser;
    }

    /**
     * Returns the default {@link TemplateParser.ErrorMode} for this Flavor.
     */
    public TemplateParser.ErrorMode getErrorMode() {
        return errorMode;
    }

    /**
     * Return default behavior for this Flavor whenever expressions must be evaluated in output tag
     * @return
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
}
