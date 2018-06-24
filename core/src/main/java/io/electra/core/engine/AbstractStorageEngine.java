package io.electra.core.engine;

import io.electra.core.data.DataStorage;
import io.electra.core.data.DataStorageImpl;
import io.electra.core.exception.EngineInitializationException;
import io.electra.core.exception.FileSystemAccessException;
import io.electra.core.index.IndexStorage;
import io.electra.core.index.IndexStorageImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Future;

/**
 * The abstraction of a storage engine that will always has to hold references to the data storage
 * and the index storage.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public abstract class AbstractStorageEngine implements StorageEngine {

    /**
     * The underlying data storage.
     */
    private DataStorage dataStorage;

    /**
     * The underlying index storage.
     */
    private IndexStorage indexStorage;

    /**
     * Create a new storage engine by its underlying data and index storage file paths.
     *
     * @param dataStoragePath  The data storage file path.
     * @param indexStoragePath The index storage file path.
     */
    public AbstractStorageEngine(Path dataStoragePath, Path indexStoragePath) throws EngineInitializationException {
        try {
            this.indexStorage = new IndexStorageImpl(indexStoragePath);
            this.dataStorage = new DataStorageImpl(dataStoragePath);
        } catch (FileSystemAccessException e) {
            throw new EngineInitializationException("Error creating engine: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        doClose();

        dataStorage.close();
        indexStorage.close();
    }

    /**
     * Will be called when the storage engine gets closed via {@link #close()}.
     */
    abstract void doClose();

    /**
     * Get the data storage.
     *
     * @return The data storage.
     */
    public DataStorage getDataStorage() {
        return dataStorage;
    }

    /**
     * Get the index storage.
     *
     * @return The index storage.
     */
    public IndexStorage getIndexStorage() {
        return indexStorage;
    }

    public abstract Future<byte[]> get(int keyHash);
}
