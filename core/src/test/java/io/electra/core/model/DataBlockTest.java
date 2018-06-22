package io.electra.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class DataBlockTest {

    private static final int TEST_NEXT_BLOCK_INDEX = 100;
    private static final int TEST_CONTENT_LENGTH = 120;
    private static final byte[] TEST_CONTENT = new byte[120];
    private DataBlock dataBlock;

    @BeforeEach
    void setUp() {
        dataBlock = new DataBlock(TEST_NEXT_BLOCK_INDEX, TEST_CONTENT_LENGTH);
        dataBlock.setContent(TEST_CONTENT);
    }

    @Test
    void getContentLength() {
        assertEquals(TEST_CONTENT_LENGTH, dataBlock.getContentLength());
    }

    @Test
    void getNextDataBlockIndex() {
        assertEquals(TEST_NEXT_BLOCK_INDEX, dataBlock.getNextDataBlockIndex());
    }

    @Test
    void getContent() {
        assertArrayEquals(TEST_CONTENT, dataBlock.getContent());
    }

    @Test
    void setContent() {
        assertArrayEquals(TEST_CONTENT, dataBlock.getContent());
    }

    @Test
    void toByteBuffer() {
        ByteBuffer test = ByteBuffer.allocate(DataBlock.DATA_BLOCK_SIZE);
        test.putInt(TEST_NEXT_BLOCK_INDEX);
        test.putInt(TEST_CONTENT_LENGTH);
        test.put(TEST_CONTENT);

        ByteBuffer byteBuffer = dataBlock.toByteBuffer();

        assertArrayEquals(test.array(), byteBuffer.array());
    }
}