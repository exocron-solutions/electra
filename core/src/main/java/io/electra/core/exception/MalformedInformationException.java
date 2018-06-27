package io.electra.core.exception;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class MalformedInformationException extends RuntimeException {

    public MalformedInformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedInformationException(String message) {
        super(message);
    }
}
