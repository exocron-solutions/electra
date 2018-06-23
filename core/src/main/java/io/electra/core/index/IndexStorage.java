package io.electra.core.index;

import io.electra.core.exception.IndexScanException;
import io.electra.core.model.Index;
import io.electra.core.storage.Storage;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface IndexStorage extends Storage {

    /**
     * Read the index at the given index in the index file.
     *
     * @param indexBlockIndex The index of the index in the file.
     *
     * @return The future of the read index.
     */
    Future<Index> readIndex(int indexBlockIndex);

    /**
     * Write an index at the given block index.
     *
     * @param indexBlockIndex The index where to write the index.
     * @param index           The index to write.
     *
     * @return The future of the written index.
     */
    Future<Index> writeIndex(int indexBlockIndex, Index index);

    /**
     * Read all indices from disk. The first index will be the index pointing to the first
     * free data block.
     *
     * @return The indices.
     */
    Future<List<Index>> readIndices() throws IndexScanException;
}
