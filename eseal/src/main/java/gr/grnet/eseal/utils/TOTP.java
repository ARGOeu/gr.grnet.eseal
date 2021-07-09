package gr.grnet.eseal.utils;

import static net.logstash.logback.argument.StructuredArguments.f;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import gr.grnet.eseal.exception.InternalServerErrorException;
import gr.grnet.eseal.logging.ServiceLogField;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TOTP {

  private static final Logger LOGGER = LoggerFactory.getLogger(TOTP.class);

  // password digit size
  private static final int PASSWORD_SIZE = 6;

  // password valid period
  private static final long PASSWORD_PERIOD = 30L;

  // hashing algorithm
  private static final HashingAlgorithm HASHING_ALGORITHM = HashingAlgorithm.SHA1;

  // code generator singleton
  private static DefaultCodeGenerator defaultCodeGenerator;

  private TOTP() {
    // not called
  }

  /**
   * @param key secret key that is being used an input to the totp generation
   * @return the generated totp
   * @throws CodeGenerationException when something goes wrong with the totp generation
   */
  public static String generate(final String key) throws CodeGenerationException {

    // check if the generator is initialized
    if (defaultCodeGenerator == null) {
      defaultCodeGenerator = new DefaultCodeGenerator(HASHING_ALGORITHM, PASSWORD_SIZE);
    }

    long unixTime = Instant.now().getEpochSecond();
    long currentBucket = Math.floorDiv(unixTime, PASSWORD_PERIOD);

    return defaultCodeGenerator.generate(key, currentBucket);
  }

  /**
   * @param key secret key that is being used an input to the totp generation
   * @param totpWaitForRefreshSeconds time (in seconds) to wait in order to generate a new totp
   * @return the generated totp
   * @throws InternalServerErrorException when something goes wrong with the totp generation
   */
  public static String generate(final String key, final long totpWaitForRefreshSeconds) {

    try {

      String totp = generate(key);

      // TODO
      // Revisit this code block as it has been provided as a temporary solution for the TOTP
      // timeout possibility and we need to re-evaluate it.
      long timePeriodRemainingSeconds = getTimePeriodRemainingSeconds();
      if (timePeriodRemainingSeconds <= totpWaitForRefreshSeconds) {
        LOGGER.info(
            "TOTP remaining time period is below/at {} seconds, {} seconds.Waiting for expiration.",
            totpWaitForRefreshSeconds,
            timePeriodRemainingSeconds,
            f(ServiceLogField.builder().build()));
        Thread.sleep(timePeriodRemainingSeconds * 1000);
        LOGGER.info("Generating new TOTP", f(ServiceLogField.builder().build()));
        totp = generate(key);
      }

      return totp;

    } catch (CodeGenerationException e) {
      LOGGER.error(
          "TOTP generator has encountered an error",
          f(ServiceLogField.builder().details(e.getMessage()).build()));
      throw new InternalServerErrorException("TOTP generator has encountered an error");
    } catch (InterruptedException ie) {
      LOGGER.error(
          "Internal thread error", f(ServiceLogField.builder().details(ie.getMessage()).build()));
      throw new InternalServerErrorException("Internal thread error");
    }
  }

  /**
   * Calculate the remaining seconds of a period with a size of {@link TOTP#PASSWORD_PERIOD} with
   * the assumption that it has started from the Unix start date
   *
   * @return remain seconds of the period
   */
  private static long getTimePeriodRemainingSeconds() {
    return PASSWORD_PERIOD - Instant.now().getEpochSecond() % PASSWORD_PERIOD;
  }
}
