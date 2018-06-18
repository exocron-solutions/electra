package io.electra.core.index;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class Index {

    /**
     * The index of the first data block.
     */
    private final int dataBlockIndex;

    /**
     * Create a new index by its underlying values.
     *
     * @param dataBlockIndex The data block index.
     */
    public Index(int dataBlockIndex) {
        this.dataBlockIndex = dataBlockIndex;
    }

    /**
     * Get the index of the first data block.
     *
     * @return The data block index.
     */
    public int getDataBlockIndex() {
        return dataBlockIndex;
    }
}
