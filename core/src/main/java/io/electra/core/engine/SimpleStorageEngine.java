package io.electra.core.engine;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import io.electra.core.exception.EngineInitializationException;
import io.electra.core.exception.IndexScanException;
import io.electra.core.model.DataRecord;
import io.electra.core.model.Index;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class SimpleStorageEngine extends AbstractStorageEngine {

    /**
     * All currently known indices.
     */
    private final Map<Integer, Index> indices = Maps.newConcurrentMap();

    /**
     * The index pointing to the first free data block.
     */
    private Index freeDataBlockIndex;

    public SimpleStorageEngine(Path dataStoragePath, Path indexStoragePath) throws EngineInitializationException {
        super(dataStoragePath, indexStoragePath);

        readIndices();
    }

    void readIndices() throws EngineInitializationException {
        Future<List<Index>> listFuture = null;

        try {
            listFuture = getIndexStorage().readIndices();
        } catch (IndexScanException e) {
            throw new EngineInitializationException("Error while reading initial indices: " + e.getMessage(), e);
        }

        List<Index> indices = Futures.getUnchecked(listFuture);
        for (Index index: indices) {
            if (index.getKeyHash() == -1) {
                freeDataBlockIndex = index;
                continue;
            }

            this.indices.put(index.getKeyHash(), index);
        }
    }

    @Override
    void doClose() {
        indices.clear();

        Futures.getUnchecked(getIndexStorage().writeIndex(0, freeDataBlockIndex));
    }

    @Override
    public Future<byte[]> get(int keyHash) {
        Index index = indices.get(keyHash);

        if (index == null) {
            return Futures.immediateFuture(null);
        }

        Future<DataRecord> dataRecordFuture = getDataStorage().readDataRecord(index);

        return Futures.lazyTransform(dataRecordFuture, input -> Objects.requireNonNull(input).getContent());
    }

    @Override
    public Future<Index> save(int keyHash, byte[] value) {
        if (indices.containsKey(keyHash)) {
            throw new IllegalStateException("Tried to save value with key hash " + keyHash + " but there is already an index with that hash.");
        }

        Index index = new Index(keyHash, freeDataBlockIndex.getBlockIndex());
        indices.put(keyHash, index);
        getIndexStorage().writeIndex(indices.size() + 2, index);

        Future<Index> indexFuture = getDataStorage().writeData(index, value);

        return Futures.lazyTransform(indexFuture, input -> {
            freeDataBlockIndex.setBlockIndex(Objects.requireNonNull(input).getBlockIndex());
            return index;
        });
    }
}
