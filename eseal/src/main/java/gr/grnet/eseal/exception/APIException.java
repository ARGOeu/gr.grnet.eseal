package gr.grnet.eseal.exception;

import org.springframework.http.HttpStatus;

/**
 * APIException holds information about the exceptions that can take place during any of the API
 * calls.
 */
public class APIException extends RuntimeException {

  private int code;
  private String message;
  private HttpStatus status;

  public APIException(int code, String message, HttpStatus status) {
    this.code = code;
    this.message = message;
    this.status = status;
  }

  public int getCode() {
    return code;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
