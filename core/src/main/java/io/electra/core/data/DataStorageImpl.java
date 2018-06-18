package io.electra.core.data;

import io.electra.core.exception.FileSystemAccessException;
import io.electra.core.filesystem.FileSystemAccessor;

import java.io.IOException;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class DataStorageImpl implements DataStorage {

    private final FileSystemAccessor fileSystemAccessor;

    public DataStorageImpl(FileSystemAccessor fileSystemAccessor) {
        this.fileSystemAccessor = fileSystemAccessor;
    }

    @Override
    public void clear() throws FileSystemAccessException {
        fileSystemAccessor.clear();
    }

    @Override
    public void close() throws IOException {
        fileSystemAccessor.close();
    }
}
