package io.electra.core.data;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import io.electra.core.exception.FileSystemAccessException;
import io.electra.core.model.DataBlock;
import io.electra.core.model.DataBlockHeader;
import io.electra.core.model.DataRecord;
import io.electra.core.model.Index;
import io.electra.core.storage.AbstractFileSystemStorage;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Arrays;
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
        Future<DataBlockHeader> dataBlockHeaderFuture = readDataBlockHeader(dataBlockIndex);

        return Futures.lazyTransform(dataBlockHeaderFuture, input -> {
            long contentPosition = getDataBlockPositionByIndex(dataBlockIndex) + 8;
            int contentLength = Objects.requireNonNull(input).getContentLength();

            Future<ByteBuffer> contentFuture = getFileSystemAccessor()
                    .read(contentPosition, contentLength);

            ByteBuffer contentBuffer = Futures.getUnchecked(contentFuture);

            return DataBlock.fromDataBlockHeaderAndContentBuffer(input, contentBuffer);
        });
    }

    /**
     * Read the header at the given data block index.
     *
     * @param dataBlockIndex The data block index.
     * @return The future of the data block header.
     */
    private Future<DataBlockHeader> readDataBlockHeader(int dataBlockIndex) {
        Future<ByteBuffer> resultFuture = getFileSystemAccessor().read(getDataBlockPositionByIndex(dataBlockIndex), 8);
        return Futures.lazyTransform(resultFuture, input -> {
            if (!Objects.requireNonNull(input).hasRemaining()) {
                return null;
            }

            return DataBlockHeader.fromByteBuffer(Objects.requireNonNull(input));
        });
    }

    @Override
    public Future<Index> writeData(Index index, byte[] data) {
        int dataBlockIndex = index.getDataBlockIndex();
        int neededBlocks = (int) Math.ceil((double) data.length / 120);
        DataRecord dataRecord = new DataRecord(dataBlockIndex);

        int[] freeBlocks = new int[neededBlocks];

        DataBlockHeader dataBlockHeader;

        for (int i = 0; i < freeBlocks.length; i++) {
            freeBlocks[i] = dataBlockIndex;

            dataBlockHeader = Futures.getUnchecked(readDataBlockHeader(dataBlockIndex));

            if (dataBlockHeader == null) {
                dataBlockIndex++;
            } else {
                dataBlockIndex = dataBlockHeader.getNextDataBlockIndex();
            }
        }

        index.setDataBlockIndex(dataBlockIndex);

        for (int i = 0; i < freeBlocks.length; i++) {
            int next = i == (freeBlocks.length - 1) ? -1 : freeBlocks[i + 1];

            int end = DataBlock.DATA_BLOCK_CONTENT_SECTION_SIZE * (i + 1);

            byte[] bytes = Arrays.copyOfRange(data, DataBlock.DATA_BLOCK_CONTENT_SECTION_SIZE * i, Math.min(data.length, end));

            dataBlockHeader = new DataBlockHeader(next, bytes.length);
            DataBlock dataBlock = DataBlock.fromDataBlockHeader(dataBlockHeader);
            dataBlock.setContent(bytes);

            dataRecord.addDataBlock(dataBlock);
        }

        ListenableFuture[] writeFutures = new ListenableFuture[dataRecord.getDataBlocks().size()];
        for (int i = 0; i < dataRecord.getDataBlocks().size(); i++) {
            writeFutures[i] = JdkFutureAdapters.listenInPoolThread(writeDataBlock(freeBlocks[i], dataRecord.getDataBlocks().get(i)));
        }

        return Futures.lazyTransform(Futures.allAsList(writeFutures), input -> index);
    }

    /**
     * Write a data block to file system.
     *
     * @param dataBlockIndex The index of the data block.
     * @param dataBlock      The data block.
     *
     * @return The future of the written bytes.
     */
    private Future<Integer> writeDataBlock(int dataBlockIndex, DataBlock dataBlock) {
        long dataBlockPositionByIndex = getDataBlockPositionByIndex(dataBlockIndex);
        return getFileSystemAccessor().write(dataBlockPositionByIndex, dataBlock.toByteBuffer());
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
