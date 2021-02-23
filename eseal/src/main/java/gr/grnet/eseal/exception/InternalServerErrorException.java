package gr.grnet.eseal.exception;

import org.springframework.http.HttpStatus;

/**
 * InternalServerErrorException extends {@link APIException} and represents all 500 or INTERNAL_SERVER_ERROR
 * exceptions that can take place in the API.
 */
public class InternalServerErrorException extends APIException {

    public InternalServerErrorException(String message) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
