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
     * A map holding all insertions.
     */
    private static final Map<String, Insertion> INSERTIONS = new HashMap<>();

    private static Insertions CURRENT_INSERTIONS = null;

    private static void updateCurrentInsertions() {
      CURRENT_INSERTIONS = Insertions.of(INSERTIONS);
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
     * Returns all tags registered for global use.
     *
     * Note that this method is unsafe, as {@link #registerInsertion(Insertion)} affects all uses
     * of this class.
     *
     * Use {@link Insertions} instead.
     *
     * @return all default tags.
     */
    @Deprecated
    public static Map<String, Insertion> getInsertions() {
        checkInitialized();
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
     * Registers an insertion for global use.
     * 
     * If an insertion exists under the same name, it is replaced by this one.
     * 
     * Note that this method is unsafe, as it affects all uses of this class.
     * Use {@link Insertions} instead.
     *
     * @param insertion
     *         the insertion to be registered.
     * @deprecated Use {@link liqp.ParseSettings.Builder#with(Insertion)}
     */
    @Deprecated
    public static void registerInsertion(Insertion insertion) {
        checkInitialized();
        INSERTIONS.put(insertion.name, insertion);
        updateCurrentInsertions();
    }
    
    static Insertions getCurrentInsertions() {
      checkInitialized();
      return CURRENT_INSERTIONS;
    }

    private static void checkInitialized() {
      if (CURRENT_INSERTIONS == null) {
        Insertions.STANDARD_INSERTIONS.writeTo(INSERTIONS);

        updateCurrentInsertions();
      }
    }
}
