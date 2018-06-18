package io.electra.core.exception;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class FileSystemAccessException extends Exception {

    public FileSystemAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
