package io.electra.core.model;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class DataBlock {

    /**
     * The size of one data block.
     */
    public static final int DATA_BLOCK_SIZE = 128;

    /**
     * The index of the next data block or -1.
     */
    private final int nextDataBlockIndex;

    /**
     * The length of the content of this block.
     */
    private final int contentLength;

    /**
     * The actual content of the data block.
     */
    private byte[] content;

    /**
     * Create a new data block by its header values.
     *
     * @param nextDataBlockIndex The index of the next data block or -1.
     * @param contentLength      The length of the content of this block.
     */
    public DataBlock(int nextDataBlockIndex, int contentLength) {
        this.nextDataBlockIndex = nextDataBlockIndex;
        this.contentLength = contentLength;
    }

    /**
     * Get the length of the content of this block.
     *
     * @return The length of the content.
     */
    public int getContentLength() {
        return contentLength;
    }

    /**
     * Get the index of the next data block.
     *
     * @return The index of the next data block.
     */
    public int getNextDataBlockIndex() {
        return nextDataBlockIndex;
    }

    /**
     * Get the content of the data block.
     *
     * @return The content.
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Set the content of the data block.
     *
     * @param content The content.
     */
    public void setContent(byte[] content) {
        this.content = content;
    }
}
