package gr.grnet.eseal.exception;

import static net.logstash.logback.argument.StructuredArguments.f;

import gr.grnet.eseal.logging.ServiceLogField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // Handle errors regrading the validation of request fields
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<APIError> handleWrongInput(
      MethodArgumentNotValidException apiEx, WebRequest request) {
    APIError errorResponse =
        new APIError(
            HttpStatus.BAD_REQUEST.value(),
            apiEx.getFieldError().getDefaultMessage(),
            HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  // Handle errors regarding malformed json in the request body
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<APIError> handleMalformedJSONException(
      HttpMessageNotReadableException apiEx, WebRequest request) {
    APIError errorResponse =
        new APIError(HttpStatus.BAD_REQUEST.value(), "Malformed JSON body", HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  // Handle errors regarding the media types that are not supported
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<APIError> handleMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException apiEx, WebRequest request) {
    APIError errorResponse =
        new APIError(
            HttpStatus.BAD_REQUEST.value(),
            apiEx.getMessage() + ". Using Content Type 'application/json' instead.",
            HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  // Handle all APIErrors that have been generated in the API
  @ExceptionHandler(APIException.class)
  public ResponseEntity<APIError> handleAPIException(APIException apiEx, WebRequest request) {
    APIError errorResponse = new APIError(apiEx.getCode(), apiEx.getMessage(), apiEx.getStatus());
    return new ResponseEntity<>(errorResponse, apiEx.getStatus());
  }

  // Generic handler for any request that isn't being handled by the rest of the handlers
  @ExceptionHandler(Exception.class)
  public ResponseEntity<APIError> handleGenericException(Exception apiEx, WebRequest request) {
    LOGGER.error(
        "Internal error occurred",
        f(ServiceLogField.builder().details(apiEx.getMessage()).build()));
    APIError errorResponse =
        new APIError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal server error",
            HttpStatus.INTERNAL_SERVER_ERROR);
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
