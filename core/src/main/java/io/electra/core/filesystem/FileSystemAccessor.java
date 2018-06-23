package io.electra.core.filesystem;

import io.electra.core.exception.FileSystemAccessException;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface FileSystemAccessor extends Closeable {

    /**
     * If the file had to be created.
     *
     * @return If the file had to be created.
     */
    boolean hadToCreateFile();

    /**
     * Clear the file we are accessing.
     *
     * @throws FileSystemAccessException If truncating fails.
     */
    void clear() throws FileSystemAccessException;

    /**
     * Read the given amount of bytes from the given offset.
     *
     * @param offset The offset.
     * @param length The amount of bytes.
     *
     * @return The byte array.
     */
    Future<ByteBuffer> read(long offset, int length);

    /**
     * Write the content of the given buffer at the given position.
     *
     * @param offset  The position.
     * @param content The data to write.
     *
     * @return The future of the amounts of bytes written.
     */
    Future<Integer> write(long offset, ByteBuffer content);

    /**
     * Get the length of the file.
     *
     * @return The length of the file.
     *
     * @throws FileSystemAccessException When the reading fails.
     */
    long getFileLength() throws FileSystemAccessException;
}
