package io.electra.core.data;

import io.electra.core.exception.FileSystemAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class DataStorageImplTest {

    private static final String TEST_FILE = "test.acc";
    private DataStorage dataStorage;

    @BeforeEach
    void setUp() throws FileSystemAccessException {
        dataStorage = new DataStorageImpl(Paths.get(TEST_FILE));
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

    @AfterEach
    void tearDown() throws IOException {
        Files.delete(Paths.get(TEST_FILE));
    }
}