package liqp.tags;

public abstract class Block extends Tag {
    /**
     * Used for all package protected blocks in the liqp.tags-package
     * whose name is their class name lower cased.
     */
    protected Block() {
        super();
    }

    /**
     * Creates a new instance of a Block.
     *
     * @param name
     *         the name of the block.
     */
    public Block(String name) {
        super(name);
    }
}
