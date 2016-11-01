package liqp.parser;

public enum Flavor {

    LIQUID("snippets"),
    JEKYLL("_includes");

    public static final String KEY = "@Flavor";

    public final String snippetsFolderName;

    Flavor(String snippetsFolderName) {
        this.snippetsFolderName = snippetsFolderName;
    }
}
