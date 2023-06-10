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

}
