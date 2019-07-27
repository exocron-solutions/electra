package io.electra.core.exception;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class MalformedHeaderException extends MalformedDataException {

  public MalformedHeaderException(String message, Throwable cause) {
    super(message, cause);
  }
}
