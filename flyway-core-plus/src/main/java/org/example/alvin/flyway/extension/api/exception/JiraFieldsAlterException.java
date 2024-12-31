package org.example.alvin.flyway.extension.api.exception;

public class JiraFieldsAlterException extends RuntimeException {

  public JiraFieldsAlterException(String message) {
    super(message);
  }

  public JiraFieldsAlterException(String message, Throwable cause) {
    super(message, cause);
  }

  public JiraFieldsAlterException(Throwable cause) {
    super(cause);
  }
}
