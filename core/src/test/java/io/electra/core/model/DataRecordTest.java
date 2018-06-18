package io.electra.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class DataRecordTest {

    private static final int TEST_FIRST_BLOCK_INDEX = 300;
    private final DataBlock dataBlock = new DataBlock(-1, 2);
    private DataRecord dataRecord;

    @BeforeEach
    void setUp() {
        dataRecord = new DataRecord(TEST_FIRST_BLOCK_INDEX);
        dataRecord.addDataBlock(dataBlock);
    }

    @Test
    void testAddDataBlock() {
        DataBlock dataBlock = new DataBlock(1, 2);
        assertEquals(1, dataRecord.getDataBlocks().size());
        dataRecord.addDataBlock(dataBlock);
        assertEquals(2, dataRecord.getDataBlocks().size());
        assertEquals(dataBlock, dataRecord.getLastDataBlock());
    }

    @Test
    void testGetLastDataBlock() {
        assertEquals(dataBlock, dataRecord.getLastDataBlock());
    }

    @Test
    void testGetFirstDataBlockIndex() {
        assertEquals(TEST_FIRST_BLOCK_INDEX, dataRecord.getFirstDataBlockIndex());
    }

    @Test
    void testGetDataBlocks() {
        assertEquals(Arrays.asList(dataBlock), dataRecord.getDataBlocks());
    }
}