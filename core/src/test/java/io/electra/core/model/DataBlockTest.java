package io.electra.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class DataBlockTest {

    private static final int TEST_NEXT_BLOCK_INDEX = 100;
    private static final int TEST_CONTENT_LENGTH = 203;
    private DataBlock dataBlock;

    @BeforeEach
    void setUp() {
        dataBlock = new DataBlock(TEST_NEXT_BLOCK_INDEX, TEST_CONTENT_LENGTH);
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
        assertNull(dataBlock.getContent());
    }

    @Test
    void setContent() {
        byte[] array = new byte[10];
        dataBlock.setContent(array);
        assertArrayEquals(array, dataBlock.getContent());
    }
}