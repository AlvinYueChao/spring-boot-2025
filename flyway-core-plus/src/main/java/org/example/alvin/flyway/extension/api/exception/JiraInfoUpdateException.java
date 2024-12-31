package org.example.alvin.flyway.extension.api.exception;

public class JiraInfoUpdateException extends RuntimeException {

  public JiraInfoUpdateException(String message) {
    super(message);
  }

  public JiraInfoUpdateException(String message, Throwable cause) {
    super(message, cause);
  }

  public JiraInfoUpdateException(Throwable cause) {
    super(cause);
  }
}
