package liqp.blocks;

import liqp.Insertion;

public abstract class Block extends Insertion {
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

    /**
     * Variant of {@link #registerInsertion(Insertion)} with strict limitation to {@link Block} subtype.
     */
    public static void registerBlock(Block block) {
        registerInsertion(block);
    }
}
