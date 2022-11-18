package liqp.parser;

import liqp.Insertions;
import liqp.ParseSettings;
import liqp.TemplateParser;
import liqp.filters.Filters;

public enum Flavor {
    LIQUID("snippets", Filters.DEFAULT_FILTERS, Insertions.STANDARD_INSERTIONS), //
    JEKYLL("_includes", Filters.JEKYLL_FILTERS, Insertions.STANDARD_INSERTIONS);

    public final String snippetsFolderName;
    private final Filters filters;
    private final Insertions insertions;
    private ParseSettings parseSettings;
    private TemplateParser parser;

    Flavor(String snippetsFolderName, Filters filters, Insertions insertions) {
        this.snippetsFolderName = snippetsFolderName;
        this.filters = filters;
        this.insertions = insertions;
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
}
