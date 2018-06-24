package io.electra.core;

import io.electra.core.exception.DatabaseInitializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class ElectraDatabaseImplTest {

    private ElectraDatabase electraDatabase;

    @BeforeEach
    void setUp() {
        try {
            electraDatabase = DatabaseFactory.createDatabase(Paths.get("."));
        } catch (DatabaseInitializationException e) {
            fail(e);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.delete(Paths.get("index.lctr"));
        Files.delete(Paths.get("data.lctr"));
    }

    @Test
    void testInstantiationWithInvalidPathWithoutAccess() {
        Executable executable = () -> electraDatabase = DatabaseFactory.createDatabase(Paths.get("/"));
        assertThrows(DatabaseInitializationException.class, executable);
    }

    @Test
    void testInstantiationWithInvalidPathFile() {
        Executable executable = () -> electraDatabase = DatabaseFactory.createDatabase(Paths.get("data.yml"));
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testClose() {
        try {
            electraDatabase.close();
        } catch (Exception e) {
            fail(e);
        }
    }
}