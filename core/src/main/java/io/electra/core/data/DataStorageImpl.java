package io.electra.core.data;

import com.google.common.util.concurrent.Futures;
import io.electra.core.exception.FileSystemAccessException;
import io.electra.core.index.Index;
import io.electra.core.model.DataBlock;
import io.electra.core.model.DataRecord;
import io.electra.core.storage.AbstractFileSystemStorage;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class DataStorageImpl extends AbstractFileSystemStorage implements DataStorage {

    public DataStorageImpl(Path dataFilePath) throws FileSystemAccessException {
        super(dataFilePath);
    }

    @Override
    protected void doClear() {

    }

    @Override
    protected void doClose() {

    }

    @Override
    public Future<DataRecord> readDataRecord(Index index) {
        int dataBlockIndex = index.getDataBlockIndex();

        DataRecord dataRecord = new DataRecord(dataBlockIndex);

        return readDataRecord(dataRecord);
    }

    /**
     * Fully read the given data record.
     *
     * @param dataRecord The data record.
     *
     * @return The future of the full data record.
     */
    private Future<DataRecord> readDataRecord(DataRecord dataRecord) {
        DataBlock lastDataBlock = dataRecord.getLastDataBlock();

        if (lastDataBlock == null) {
            return Futures.lazyTransform(readDataBlock(dataRecord.getFirstDataBlockIndex()), input -> {
                dataRecord.addDataBlock(input);
                return Futures.getUnchecked(readDataRecord(dataRecord));
            });
        }

        int nextDataBlockIndex = lastDataBlock.getNextDataBlockIndex();

        if (nextDataBlockIndex == -1) {
            return Futures.immediateFuture(dataRecord);
        }

        return Futures.lazyTransform(readDataBlock(nextDataBlockIndex), input -> {
            dataRecord.addDataBlock(input);
            return Futures.getUnchecked(readDataRecord(dataRecord));
        });
    }

    /**
     * Read a single data block from the file system.
     *
     * @param dataBlockIndex The index of the data block we want to read.
     *
     * @return The data block.
     */
    private Future<DataBlock> readDataBlock(int dataBlockIndex) {
        Future<ByteBuffer> resultFuture = getFileSystemAccessor().read(getDataBlockPositionByIndex(dataBlockIndex), 8);

        Future<Future<DataBlock>> resultResolution = Futures.lazyTransform(resultFuture, byteBuffer -> {
            DataBlock dataBlock = createDataBlock(Objects.requireNonNull(byteBuffer));

            Future<ByteBuffer> contentFuture = getFileSystemAccessor()
                    .read(getDataBlockPositionByIndex(dataBlockIndex) + 8, dataBlock.getContentLength());

            return Futures.lazyTransform(contentFuture, byteBuffer1 -> {
                byte[] content = new byte[dataBlock.getContentLength()];
                Objects.requireNonNull(byteBuffer1).get(content);
                dataBlock.setContent(content);
                return dataBlock;
            });
        });

        return Futures.lazyTransform(resultResolution, Futures::getUnchecked);
    }

    /**
     * Read a data block instance from its byte buffer.
     *
     * @param byteBuffer The byte buffer.
     *
     * @return The data block.
     */
    private DataBlock createDataBlock(ByteBuffer byteBuffer) {
        int nextDataBlockIndex = byteBuffer.getInt();
        int contentLength = byteBuffer.getInt();

        return new DataBlock(nextDataBlockIndex, contentLength);
    }

    /**
     * Get the final position of a data block by its index. The position will always be data block size * data block index.
     *
     * @param dataBlockIndex The index of the data block.
     *
     * @return The data block position.
     */
    private long getDataBlockPositionByIndex(int dataBlockIndex) {
        return DataBlock.DATA_BLOCK_SIZE * dataBlockIndex;
    }
}
