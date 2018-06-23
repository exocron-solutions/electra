package io.electra.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class DataRecordTest {

    private static final String TEST_DATA_BLOCK_CONTENT = "ogpagehiaio+gawga+wghwag+paowgh+aogh";
    private static final int TEST_FIRST_BLOCK_INDEX = 300;
    private final DataBlock dataBlock = new DataBlock(-1, TEST_DATA_BLOCK_CONTENT.getBytes().length);
    private DataRecord dataRecord;

    @BeforeEach
    void setUp() {
        dataBlock.setContent("he".getBytes());
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

    @Test
    void testGetContent() {
        assertEquals(dataBlock.getContent(), dataRecord.getContent());
    }
}