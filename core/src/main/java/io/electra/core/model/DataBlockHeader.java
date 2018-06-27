package io.electra.core.model;

import io.electra.core.exception.MalformedHeaderException;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class DataBlockHeader {

    /**
     * The index of the next data block or -1.
     */
    private final int nextDataBlockIndex;

    /**
     * The length of the content of this block.
     */
    private final int contentLength;

    /**
     * Create a header by its underlying data.
     *
     * @param nextDataBlockIndex The index if the next data block.
     * @param contentLength      The length of the content.
     */
    public DataBlockHeader(int nextDataBlockIndex, int contentLength) {
        this.nextDataBlockIndex = nextDataBlockIndex;
        this.contentLength = contentLength;
    }

    /**
     * Create a data block header by a byte buffer.
     *
     * @param byteBuffer The byte buffer.
     *
     * @return The data block header instance.
     */
    public static DataBlockHeader fromByteBuffer(ByteBuffer byteBuffer) {
        try {
            int nextDataBlockIndex = byteBuffer.getInt();
            int contentLength = byteBuffer.getInt();

            return new DataBlockHeader(nextDataBlockIndex, contentLength);
        } catch (BufferUnderflowException e) {
            throw new MalformedHeaderException("Error while reading block header from byte buffer.", e);
        }
    }

    /**
     * Convert a data block header to a byte buffer. The byte buffer os ready for reading.
     *
     * @return The byte buffer.
     */
    public ByteBuffer toByteBuffer() {
        ByteBuffer byteBuffer = toByteBuffer(ByteBuffer.allocate(8));
        byteBuffer.flip();
        return byteBuffer;
    }

    /**
     * Convert a data block header to a byte buffer. The byte buffer will remain in write state.
     *
     * @param byteBuffer A given byte buffer.
     *
     * @return The byte buffer.
     */
    public ByteBuffer toByteBuffer(ByteBuffer byteBuffer) {
        byteBuffer.putInt(nextDataBlockIndex);
        byteBuffer.putInt(contentLength);
        return byteBuffer;
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
}
