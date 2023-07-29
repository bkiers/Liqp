package liqp;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import liqp.nodes.LNode;

/**
 * Basic class for both tags and blocks. Most of internal api uses this. 
 */
public abstract class Insertion extends LValue {

    /**
     * The name of this insertions.
     */
    public final String name;
    
    /**
     * Used for all package protected insertions in the liqp.insertions-package
     * whose name is their class name lower cased.
     */
    protected Insertion() {
        this.name = this.getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
    }


    /**
     * Creates a new instance of a Insertion.
     *
     * @param name
     *         the name of the tag.
     */
    public Insertion(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this insertion.
     *
     * @return The name.
     */
    public String getName() {
      return name;
    }

    /**
     * Renders this insertion.
     *
     * @param context
     *         the context (variables) with which this
     *         node should be rendered.
     * @param nodes
     *         the nodes of this tag is created with. See
     *         the file `src/grammar/LiquidWalker.g` to see
     *         how each of the tags is created.
     *
     * @return an Object denoting the rendered AST.
     */
    public abstract Object render(TemplateContext context, LNode... nodes);
}
