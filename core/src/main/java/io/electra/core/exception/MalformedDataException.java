package io.electra.core.exception;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class MalformedDataException extends MalformedInformationException {

  public MalformedDataException(String message, Throwable cause) {
    super(message, cause);
  }

  public MalformedDataException(String message) {
    super(message);
  }
}
