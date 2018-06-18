package io.electra.core.data;

import com.google.common.util.concurrent.Futures;
import io.electra.core.exception.FileSystemAccessException;
import io.electra.core.filesystem.FileSystemAccessor;
import io.electra.core.index.Index;
import io.electra.core.model.DataBlock;
import io.electra.core.model.DataRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class DataStorageImplTest {

    private static final int TEST_CHAIN_BLOCK_INDEX1 = 0;
    private static final int TEST_CHAIN_BLOCK_INDEX2 = 2;
    private static final int TEST_SINGLE_BLOCK_INDEX = 1;
    private static final byte[] TEST_SINGLE_BLOCK_CONTENT = "I am a tester moarfuckn king.".getBytes();
    private static final byte[] TEST_CHAIN_BLOCK_CONTENT1 = "IjajajajajajajajajajajajajjajaIjajajajajajajajajajajajajjajaIjajajajajajajajajajajajajjajaIjajajajajajajajajajajajajjaja".getBytes();
    private static final byte[] TEST_CHAIN_BLOCK_CONTENT2 = "IjajajajajajajajajajajajajjajaIjajajajajajajajajajajajajjajaIjajajajajajajajajajajajajjajaIjajajajajajajajajajajajajjaja".getBytes();

    private static final String TEST_FILE = "test.acc";
    private DataStorage dataStorage;

    @BeforeEach
    void setUp() throws FileSystemAccessException {
        dataStorage = new DataStorageImpl(Paths.get(TEST_FILE));

        FileSystemAccessor fileSystemAccessor = ((DataStorageImpl) dataStorage).getFileSystemAccessor();

        ByteBuffer byteBuffer = ByteBuffer.allocate(DataBlock.DATA_BLOCK_SIZE);
        byteBuffer.putInt(-1);
        byteBuffer.putInt(TEST_SINGLE_BLOCK_CONTENT.length);
        byteBuffer.put(TEST_SINGLE_BLOCK_CONTENT);
        byteBuffer.flip();

        Futures.getUnchecked(fileSystemAccessor.write(DataBlock.DATA_BLOCK_SIZE * TEST_SINGLE_BLOCK_INDEX, byteBuffer));

        byteBuffer = ByteBuffer.allocate(DataBlock.DATA_BLOCK_SIZE);
        byteBuffer.putInt(2);
        byteBuffer.putInt(TEST_CHAIN_BLOCK_CONTENT1.length);
        byteBuffer.put(TEST_CHAIN_BLOCK_CONTENT1);
        byteBuffer.flip();

        Futures.getUnchecked(fileSystemAccessor.write(DataBlock.DATA_BLOCK_SIZE * TEST_CHAIN_BLOCK_INDEX1, byteBuffer));

        byteBuffer = ByteBuffer.allocate(DataBlock.DATA_BLOCK_SIZE);
        byteBuffer.putInt(-1);
        byteBuffer.putInt(TEST_CHAIN_BLOCK_CONTENT2.length);
        byteBuffer.put(TEST_CHAIN_BLOCK_CONTENT2);
        byteBuffer.flip();

        Futures.getUnchecked(fileSystemAccessor.write(DataBlock.DATA_BLOCK_SIZE * TEST_CHAIN_BLOCK_INDEX2, byteBuffer));
    }

    @Test
    void testClear() {
        try {
            dataStorage.clear();
        } catch (FileSystemAccessException e) {
            fail(e);
        }
    }

    @Test
    void testClose() {
        try {
            dataStorage.close();
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void testReadDataRecord() throws ExecutionException, InterruptedException {
        Index index = new Index(TEST_SINGLE_BLOCK_INDEX);

        Future<DataRecord> dataRecordFuture = dataStorage.readDataRecord(index);
        DataRecord dataRecord = dataRecordFuture.get();

        List<DataBlock> dataBlocks = dataRecord.getDataBlocks();

        DataBlock dataBlock = dataBlocks.get(0);

        assertEquals(-1, dataBlock.getNextDataBlockIndex());
        assertEquals(TEST_SINGLE_BLOCK_CONTENT.length, dataBlock.getContentLength());
        assertArrayEquals(TEST_SINGLE_BLOCK_CONTENT, dataBlock.getContent());
    }

    @Test
    void testReadDataRecordWithDataBlockChain() throws ExecutionException, InterruptedException {
        Index index = new Index(TEST_CHAIN_BLOCK_INDEX1);

        Future<DataRecord> dataRecordFuture = dataStorage.readDataRecord(index);
        DataRecord dataRecord = dataRecordFuture.get();

        List<DataBlock> dataBlocks = dataRecord.getDataBlocks();

        DataBlock dataBlock = dataBlocks.get(0);

        assertEquals(TEST_CHAIN_BLOCK_INDEX2, dataBlock.getNextDataBlockIndex());
        assertEquals(TEST_CHAIN_BLOCK_CONTENT1.length, dataBlock.getContentLength());
        assertArrayEquals(TEST_CHAIN_BLOCK_CONTENT1, dataBlock.getContent());

        dataBlock = dataBlocks.get(1);

        assertEquals(-1, dataBlock.getNextDataBlockIndex());
        assertEquals(TEST_CHAIN_BLOCK_CONTENT2.length, dataBlock.getContentLength());
        assertArrayEquals(TEST_CHAIN_BLOCK_CONTENT2, dataBlock.getContent());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.delete(Paths.get(TEST_FILE));
    }
}