package io.electra.core.storage;

import io.electra.core.exception.FileSystemAccessException;

import java.io.Closeable;

/**
 * Defines what a general storage has to be capable of. The main part of the storage functionalities is the data
 * safety. Therefor it should implement {@link Closeable} for persisting its data at any time.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface Storage extends Closeable {

    /**
     * Clear up the whole storage. Use with caution as this will result in instant complete data loss.
     */
    void clear() throws FileSystemAccessException;
}
