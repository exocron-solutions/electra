package io.electra.core.index;

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
class IndexStorageImplTest {

    private static final String TEST_FILE = "test.acc";
    private IndexStorage indexStorage;

    @BeforeEach
    void setUp() throws FileSystemAccessException {
        indexStorage = new IndexStorageImpl(Paths.get(TEST_FILE));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.delete(Paths.get(TEST_FILE));
    }

    @Test
    void testDoClear() {
        try {
            indexStorage.clear();
        } catch (FileSystemAccessException e) {
            fail();
        }
    }

    @Test
    void testDoClose() {
        try {
            indexStorage.close();
        } catch (IOException e) {
            fail();
        }
    }
}