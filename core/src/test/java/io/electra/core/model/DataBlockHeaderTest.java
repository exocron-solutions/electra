package io.electra.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class DataBlockHeaderTest {

    private static final int TEST_NEXT_BLOCK_INDEX = 200;
    private static final int TEST_CONTENT_LENGTH = 300;
    private DataBlockHeader dataBlockHeader;

    @BeforeEach
    void setUp() {
        dataBlockHeader = new DataBlockHeader(TEST_NEXT_BLOCK_INDEX, TEST_CONTENT_LENGTH);
    }

    @Test
    void testFromByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.putInt(TEST_NEXT_BLOCK_INDEX);
        byteBuffer.putInt(TEST_CONTENT_LENGTH);
        byteBuffer.flip();

        DataBlockHeader dataBlockHeader = DataBlockHeader.fromByteBuffer(byteBuffer);

        assertEquals(TEST_CONTENT_LENGTH, dataBlockHeader.getContentLength());
        assertEquals(TEST_NEXT_BLOCK_INDEX, dataBlockHeader.getNextDataBlockIndex());
    }

    @Test
    void getNextDataBlockIndex() {
        assertEquals(TEST_NEXT_BLOCK_INDEX, dataBlockHeader.getNextDataBlockIndex());
    }

    @Test
    void getContentLength() {
        assertEquals(TEST_CONTENT_LENGTH, dataBlockHeader.getContentLength());
    }
}