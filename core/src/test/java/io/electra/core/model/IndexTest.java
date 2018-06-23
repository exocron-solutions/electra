package io.electra.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class IndexTest {

    private static final int TEST_KEY_HASH = 90;
    private static final int TEST_DATA_BLOCK_INDEX = 300;
    private Index index;

    @BeforeEach
    void setUp() {
        index = new Index(TEST_KEY_HASH, TEST_DATA_BLOCK_INDEX);
    }

    @Test
    void testGetDataBlockIndex() {
        assertEquals(TEST_DATA_BLOCK_INDEX, index.getDataBlockIndex());
    }

    @Test
    void testSetDataBlockIndex() {
        index.setDataBlockIndex(TEST_DATA_BLOCK_INDEX + 1);

        assertEquals(TEST_DATA_BLOCK_INDEX + 1, index.getDataBlockIndex());
    }

    @Test
    void testGetKeyHash() {
        assertEquals(TEST_KEY_HASH, index.getKeyHash());
    }

    @Test
    void testFromByteBuffer() {
        ByteBuffer byteBuffer = getTestByteBuffer();

        Index indexFromByteBuffer = Index.fromByteBuffer(byteBuffer);

        assertEquals(TEST_KEY_HASH, indexFromByteBuffer.getKeyHash());
        assertEquals(TEST_DATA_BLOCK_INDEX, indexFromByteBuffer.getDataBlockIndex());
    }

    @Test
    void testToByteBuffer() {
        ByteBuffer indexToByteBuffer = index.toByteBuffer();

        assertArrayEquals(getTestByteBuffer().array(), indexToByteBuffer.array());
    }

    private ByteBuffer getTestByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Index.INDEX_BLOCK_SIZE);
        byteBuffer.putInt(TEST_KEY_HASH);
        byteBuffer.putInt(TEST_DATA_BLOCK_INDEX);
        byteBuffer.flip();
        return byteBuffer;
    }
}