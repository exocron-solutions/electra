package io.electra.core.model;

import java.nio.ByteBuffer;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class Index {

    /**
     * How big an index will be in bytes in its byte buffer representation.
     */
    public static final int INDEX_BLOCK_SIZE = 9;

    /**
     * The hash of the this value belongs to.
     */
    private final int keyHash;

    /**
     * The index of the first data block.
     */
    private int dataBlockIndex;

    /**
     * Create a new index by its underlying values.
     *
     * @param keyHash The hash of the this value belongs to.
     * @param dataBlockIndex The data block index.
     */
    public Index(int keyHash, int dataBlockIndex) {
        this.keyHash = keyHash;
        this.dataBlockIndex = dataBlockIndex;
    }

    /**
     * Read all data from the given byte buffer and create a new index from it.
     *
     * @param byteBuffer The byte buffer.
     *
     * @return The index instance.
     */
    public static Index fromByteBuffer(ByteBuffer byteBuffer) {
        int keyHash = byteBuffer.getInt();
        int dataBlockIndex = byteBuffer.getInt();
        return new Index(keyHash, dataBlockIndex);
    }

    /**
     * Get the hash of the this value belongs to.
     *
     * @return The hash of the this value belongs to.
     */
    public int getKeyHash() {
        return keyHash;
    }

    /**
     * Get the index of the first data block.
     *
     * @return The data block index.
     */
    public int getDataBlockIndex() {
        return dataBlockIndex;
    }

    /**
     * Set the index of the data block.
     *
     * @param dataBlockIndex The index of the data block.
     */
    public void setDataBlockIndex(int dataBlockIndex) {
        this.dataBlockIndex = dataBlockIndex;
    }

    /**
     * Write this index to byte buffer, that will be ready for reading.
     *
     * @return The byte buffer.
     */
    public ByteBuffer toByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(INDEX_BLOCK_SIZE);
        byteBuffer.putInt(keyHash);
        byteBuffer.putInt(dataBlockIndex);
        byteBuffer.flip();
        return byteBuffer;
    }
}
