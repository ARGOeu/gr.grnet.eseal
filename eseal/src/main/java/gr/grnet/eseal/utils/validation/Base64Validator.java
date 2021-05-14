package gr.grnet.eseal.utils.validation;

import java.nio.charset.StandardCharsets;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * This validator {@link Base64Validator} is responsible to check whether a class field is Base64
 * encoded or not
 */
public class Base64Validator implements ConstraintValidator<Base64, String> {
  @Override
  public void initialize(Base64 constraintAnnotation) {}

  @Override
  public boolean isValid(
      String base64field, ConstraintValidatorContext constraintValidatorContext) {

    try {
      java.util.Base64.getDecoder().decode(base64field.getBytes(StandardCharsets.UTF_8));
    } catch (IllegalArgumentException exc) {
      //      // disable existing violation message
      //      constraintValidatorContext.disableDefaultConstraintViolation();
      //      // build new violation message and add it
      //      constraintValidatorContext
      //          .buildConstraintViolationWithTemplate("message")
      //          .addConstraintViolation();
      return false;
    }
    return true;
  }
}
