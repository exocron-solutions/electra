package io.electra.core.model;

import io.electra.core.exception.MalformedIndexException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class IndexTest {

    private static final int TEST_KEY_HASH = 90;
    private static final int TEST_DATA_BLOCK_INDEX = 300;
    private static final boolean TEST_IS_EMPTY = false;
    private Index index;

    @BeforeEach
    void setUp() {
        index = new Index(TEST_KEY_HASH, TEST_DATA_BLOCK_INDEX);
        index.setEmpty(false);
    }

    @Test
    void testGetDataBlockIndex() {
        assertEquals(TEST_DATA_BLOCK_INDEX, index.getBlockIndex());
    }

    @Test
    void testSetDataBlockIndex() {
        index.setBlockIndex(TEST_DATA_BLOCK_INDEX + 1);

        assertEquals(TEST_DATA_BLOCK_INDEX + 1, index.getBlockIndex());
    }

    @Test
    void testIsEmpty() {
        assertEquals(TEST_IS_EMPTY, index.isEmpty());
    }

    @Test
    void testSetEmpty() {
        index.setEmpty(!TEST_IS_EMPTY);

        assertEquals(!TEST_IS_EMPTY, index.isEmpty());
    }

    @Test
    void testGetKeyHash() {
        assertEquals(TEST_KEY_HASH, index.getKeyHash());
    }

    @Test
    void testFromByteBuffer() {
        ByteBuffer byteBuffer = getTestByteBuffer(false);

        Index indexFromByteBuffer = Index.fromByteBuffer(byteBuffer);

        assertEquals(TEST_KEY_HASH, indexFromByteBuffer.getKeyHash());
        assertEquals(TEST_DATA_BLOCK_INDEX, indexFromByteBuffer.getBlockIndex());
    }

    @Test
    void testToByteBuffer() {
        ByteBuffer indexToByteBuffer = index.toByteBuffer();

        assertArrayEquals(getTestByteBuffer(false).array(), indexToByteBuffer.array());
    }

    @Test
    void testFromByteBufferEmpty() {
        ByteBuffer byteBuffer = getTestByteBuffer(true);

        Index indexFromByteBuffer = Index.fromByteBuffer(byteBuffer);

        assertEquals(TEST_KEY_HASH, indexFromByteBuffer.getKeyHash());
        assertTrue(indexFromByteBuffer.isEmpty());
        assertEquals(TEST_DATA_BLOCK_INDEX, indexFromByteBuffer.getBlockIndex());
    }

    @Test
    void testToByteBufferEmpty() {
        index.setEmpty(true);
        ByteBuffer indexToByteBuffer = index.toByteBuffer();

        assertArrayEquals(getTestByteBuffer(true).array(), indexToByteBuffer.array());
    }

    @Test
    void testFromByteBufferWithInvalidByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(3);

        Executable executable = () -> Index.fromByteBuffer(byteBuffer);

        assertThrows(MalformedIndexException.class, executable);
    }

    private ByteBuffer getTestByteBuffer(boolean empty) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Index.INDEX_BLOCK_SIZE);
        byteBuffer.putInt(TEST_KEY_HASH);
        byteBuffer.putInt(TEST_DATA_BLOCK_INDEX);
        byteBuffer.put((byte) (empty ? 1 : 0));
        byteBuffer.flip();
        return byteBuffer;
    }
}