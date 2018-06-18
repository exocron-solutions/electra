package io.electra.core.data;

import io.electra.core.exception.FileSystemAccessException;
import io.electra.core.filesystem.DuplexAsynchronousFileChannelFileSystemAccessor;
import io.electra.core.filesystem.FileSystemAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class DataStorageImplTest {

    private static final String TEST_FILE = "test.acc";
    private DataStorage dataStorage;

    @BeforeEach
    void setUp() throws FileSystemAccessException {
        FileSystemAccessor fileSystemAccessor = new DuplexAsynchronousFileChannelFileSystemAccessor(Paths.get(TEST_FILE));
        dataStorage = new DataStorageImpl(fileSystemAccessor);
    }

    @Test
    void clear() throws FileSystemAccessException {
        dataStorage.clear();
    }

    @Test
    void close() throws IOException {
        dataStorage.close();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.delete(Paths.get(TEST_FILE));
    }
}