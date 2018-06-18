package io.electra.core.index;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class IndexTest {

    private static final int TEST_DATA_BLOCK_INDEX = 300;
    private Index index;

    @BeforeEach
    void setUp() {
        index = new Index(TEST_DATA_BLOCK_INDEX);
    }

    @Test
    void getDataBlockIndex() {
        assertEquals(TEST_DATA_BLOCK_INDEX, index.getDataBlockIndex());
    }
}