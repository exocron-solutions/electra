package io.electra.core.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class DataRecord {

    /**
     * The index of the first block of the record.
     */
    private final int firstDataBlockIndex;

    /**
     * The data blocks of this data record.
     */
    private final LinkedList<DataBlock> dataBlocks = new LinkedList<>();

    /**
     * Create a new data record.
     *
     * @param firstDataBlockIndex The index of the first block of the record.
     */
    public DataRecord(int firstDataBlockIndex) {
        this.firstDataBlockIndex = firstDataBlockIndex;
    }

    /**
     * Add the given data block to the record.
     *
     * @param dataBlock The data block.
     */
    public void addDataBlock(DataBlock dataBlock) {
        dataBlocks.add(dataBlock);
    }

    /**
     * Get the last data block of the cord.
     *
     * @return The last data block.
     */
    public DataBlock getLastDataBlock() {
        if (dataBlocks.isEmpty()) {
            return null;
        }

        return dataBlocks.getLast();
    }

    /**
     * Get the index of the first block of the record.
     *
     * @return The index of the first block of the record.
     */
    public int getFirstDataBlockIndex() {
        return firstDataBlockIndex;
    }

    /**
     * Get all data blocks.
     *
     * @return The data blocks.
     */
    public List<DataBlock> getDataBlocks() {
        return dataBlocks;
    }
}
