package liqp;

import liqp.blocks.Capture;
import liqp.blocks.Case;
import liqp.blocks.Comment;
import liqp.blocks.Cycle;
import liqp.blocks.For;
import liqp.blocks.If;
import liqp.blocks.Ifchanged;
import liqp.blocks.Raw;
import liqp.blocks.Tablerow;
import liqp.blocks.Unless;
import liqp.tags.Assign;
import liqp.tags.Break;
import liqp.tags.Continue;
import liqp.tags.Decrement;
import liqp.tags.Include;
import liqp.tags.Increment;
import liqp.nodes.LNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic class for both tags and blocks. Most of internal api uses this. 
 */
public abstract class Insertion extends LValue {

    /**
     * A map holding all insertions.
     */
    private static final Map<String, Insertion> INSERTIONS = new HashMap<>();

    static {
        // Register all standard insertions.
        registerInsertion(new Assign());
        registerInsertion(new Break());
        registerInsertion(new Capture());
        registerInsertion(new Case());
        registerInsertion(new Comment());
        registerInsertion(new Continue());
        registerInsertion(new Cycle());
        registerInsertion(new Decrement());
        registerInsertion(new For());
        registerInsertion(new If());
        registerInsertion(new Ifchanged());
        registerInsertion(new Include());
        registerInsertion(new Increment());
        registerInsertion(new Raw());
        registerInsertion(new Tablerow());
        registerInsertion(new Unless());
    }
    
    /**
     * The name of this insertions.
     */
    public final String name;
    
    /**
     * Used for all package protected insertions in the liqp.insertions-package
     * whose name is their class name lower cased.
     */
    protected Insertion() {
        this.name = this.getClass().getSimpleName().toLowerCase();
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
     * Returns all default tags.
     *
     * @return all default tags.
     */
    public static Map<String, Insertion> getInsertions() {
        return new HashMap<>(INSERTIONS);
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


    /**
     * Registers a new insertion.
     *
     * @param insertion
     *         the insertion to be registered.
     */
    public static void registerInsertion(Insertion insertion) {
        INSERTIONS.put(insertion.name, insertion);
    }
    
}
