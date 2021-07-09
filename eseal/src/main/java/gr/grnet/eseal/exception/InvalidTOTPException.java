package gr.grnet.eseal.exception;

/**
 * InvalidTOTPException extends {@link UnprocessableEntityException} and represents the case of a
 * totp generated with the wrong key or a TOTP that has expired.
 */
public class InvalidTOTPException extends UnprocessableEntityException {

  public InvalidTOTPException() {
    super("Invalid key or expired TOTP");
  }
}
