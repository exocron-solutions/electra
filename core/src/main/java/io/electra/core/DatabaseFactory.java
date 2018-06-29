package io.electra.core;

import io.electra.core.exception.DatabaseInitializationException;
import io.electra.core.exception.EngineInitializationException;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class DatabaseFactory {

    DatabaseFactory() {
        throw new AssertionError("Can't instantiate factories.");
    }

    public static ElectraDatabase createDatabase(Path databaseFolder) throws DatabaseInitializationException {
        if (!Files.isDirectory(databaseFolder)) {
            throw new IllegalArgumentException("The given database folder is not a directory.");
        }

        try {
            return new ElectraDatabaseImpl(databaseFolder);
        } catch (EngineInitializationException e) {
            throw new DatabaseInitializationException("Error loading storage engine", e);
        }
    }
}
