package org.example.alvin.flyway.extension.api.exception;

public class JiraInfoReadException extends RuntimeException {

  public JiraInfoReadException(String message) {
    super(message);
  }

  public JiraInfoReadException(String message, Throwable cause) {
    super(message, cause);
  }

  public JiraInfoReadException(Throwable cause) {
    super(cause);
  }
}
