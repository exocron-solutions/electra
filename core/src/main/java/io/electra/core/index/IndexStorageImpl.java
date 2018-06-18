package io.electra.core.index;

import io.electra.core.exception.FileSystemAccessException;
import io.electra.core.storage.AbstractFileSystemStorage;

import java.nio.file.Path;

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
}
