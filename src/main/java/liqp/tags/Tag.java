package liqp.tags;

import liqp.Insertion;

/**
 * Tags are used for the logic in a template.
 */
public abstract class Tag extends Insertion {

    protected Tag() {
        super();
    }

    /**
     * Creates a new instance of a Tag.
     *
     * @param name
     *         the name of the tag.
     */
    public Tag(String name) {
        super(name);
    }

    /**
     * Variant of {@link #registerInsertion(Insertion)} with strict limitation to {@link Tag} subtype.
     * For clear API and backward compatibility.
     * 
     * @deprecated Use {@link liqp.ParseSettings.Builder#with(Insertion)}.
     */
    @Deprecated
    public static void registerTag(Tag tag) {
        registerInsertion(tag);
    }


    /**
     * Retrieves a tag with a specific name.
     *
     * @param name
     *         the name of the tag to retrieve.
     *
     * @return a tag with a specific name.
     * @deprecated Use Insertions#get(String)
     */
    @Deprecated
    public static Insertion getTag(String name) {
        Insertion tag = getInsertions().get(name);

        if (tag == null) {
            throw new RuntimeException("unknown tag: " + name);
        }

        return tag;
    }
    
}
