package io.electra.core.index;

import com.google.common.util.concurrent.Futures;
import io.electra.core.exception.FileSystemAccessException;
import io.electra.core.model.Index;
import io.electra.core.storage.AbstractFileSystemStorage;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.concurrent.Future;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class IndexStorageImpl extends AbstractFileSystemStorage implements IndexStorage {

    public IndexStorageImpl(Path indexFilePath) throws FileSystemAccessException {
        super(indexFilePath);
    }

    @Override
    protected void doClear() {

    }

    @Override
    protected void doClose() {

    }

    @Override
    public Future<Index> readIndex(int indexBlockIndex) {
        long positionByIndex = getIndexBlockPositionByIndex(indexBlockIndex);
        Future<ByteBuffer> bufferFuture = getFileSystemAccessor().read(positionByIndex, Index.INDEX_BLOCK_SIZE);
        return Futures.lazyTransform(bufferFuture, Index::fromByteBuffer);
    }

    @Override
    public Future<Index> writeIndex(int indexBlockIndex, Index index) {
        long positionByIndex = getIndexBlockPositionByIndex(indexBlockIndex);
        ByteBuffer byteBuffer = index.toByteBuffer();
        Future<Integer> writeFuture = getFileSystemAccessor().write(positionByIndex, byteBuffer);
        return Futures.lazyTransform(writeFuture, input -> index);
    }

    /**
     * Get the final position of an index block by its index. The position will always be index block size * index block index.
     *
     * @param indexBlockIndex The index of the index block.
     *
     * @return The index block position.
     */
    private long getIndexBlockPositionByIndex(int indexBlockIndex) {
        return Index.INDEX_BLOCK_SIZE * indexBlockIndex;
    }
}
