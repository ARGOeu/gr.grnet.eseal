package gr.grnet.eseal.utils;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.CodeGenerationException;

import java.time.Instant;

public final class TOTP {

    // password digit size
    private static final int PASSWORD_SIZE = 6;

    // password valid period
    private static final long PASSWORD_PERIOD = 30L;

    // hashing algorithm
    private static final HashingAlgorithm HASHING_ALGORITHM = HashingAlgorithm.SHA1;

    // code generator singleton
    private static DefaultCodeGenerator defaultCodeGenerator;

    private TOTP() {
        //not called
    }


    /**
     *
     * @param key, secret key that is being used an input to the totp generation
     * @return the generated totp
     * @throws CodeGenerationException when something goes wrong with the totp generation
     */
    public static String generate(final String key) throws CodeGenerationException {

        // check if the generator is initialized
        if (defaultCodeGenerator == null) {
            defaultCodeGenerator =  new DefaultCodeGenerator(HASHING_ALGORITHM, PASSWORD_SIZE);
        }

        long unixTime = Instant.now().getEpochSecond();
        long currentBucket = Math.floorDiv(unixTime, PASSWORD_PERIOD);

        return defaultCodeGenerator.generate(key, currentBucket);
    }

    /**
     * Calculate the remaining seconds of a period with a size of {@link TOTP#PASSWORD_PERIOD}
     * with the assumption that it has started from the Unix start date
     * @return remain seconds of the period
     */
    public static long getTimePeriodRemainingSeconds() {
        return PASSWORD_PERIOD - Instant.now().getEpochSecond() % PASSWORD_PERIOD;

    }
}
