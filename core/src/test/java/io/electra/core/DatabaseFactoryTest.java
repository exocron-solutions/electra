package io.electra.core;

import io.electra.core.exception.DatabaseInitializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class DatabaseFactoryTest {

    @Test
    void testCreateDatabase() {
        try {
            ElectraDatabase database = DatabaseFactory.createDatabase(Paths.get("./"));
            assertNotNull(database);
        } catch (DatabaseInitializationException e) {
            fail(e);
        }
    }

    @Test
    void testAssertionErrorOnInit() {
        Executable executable = DatabaseFactory::new;
        assertThrows(AssertionError.class, executable);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get("index.lctr"));
        Files.deleteIfExists(Paths.get("data.lctr"));
    }
}