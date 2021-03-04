package gr.grnet.eseal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle errors regrading the validation of request fields
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIError> handleWrongInput(MethodArgumentNotValidException apiEx, WebRequest request) {
        APIError errorResponse = new APIError(HttpStatus.BAD_REQUEST.value(), apiEx.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle errors regarding malformed json in the request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIError> handleMalformedJSONException(HttpMessageNotReadableException apiEx, WebRequest request) {
        APIError errorResponse = new APIError(HttpStatus.BAD_REQUEST.value(),"Malformed JSON body", HttpStatus.BAD_REQUEST);
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
        APIError errorResponse = new APIError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
