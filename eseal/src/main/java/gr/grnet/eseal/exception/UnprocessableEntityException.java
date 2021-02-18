package gr.grnet.eseal.exception;

import org.springframework.http.HttpStatus;

/**
 * UnprocessableEntityException extends {@link APIException} and represents all 422 or UNPROCESSABLE_ENTITY
 * exceptions that can take place in the API.
 */
public class UnprocessableEntityException extends APIException {

    public UnprocessableEntityException(String message) {
        super(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                message,
                HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
