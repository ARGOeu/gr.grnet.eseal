package gr.grnet.eseal.utils;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.CodeGenerationException;

import java.time.Instant;

public class TOTP {

    // password digit size
    private static final int passwordSize = 6;

    // password valid period
    private static final long passwordPeriod = 30L;

    // hashing algorithm
    private static final HashingAlgorithm hashingAlgorithm = HashingAlgorithm.SHA1;

    // code generator singleton
    private static DefaultCodeGenerator defaultCodeGenerator;

    /**
     *
     * @param key, secret key that is being used an input to the totp generation
     * @return the generated totp
     * @throws CodeGenerationException when something goes wrong with the totp generation
     */
    public static String generate(String key) throws CodeGenerationException{

        // check if the generator is initialized
        if (defaultCodeGenerator == null) {
            defaultCodeGenerator =  new DefaultCodeGenerator(hashingAlgorithm, passwordSize);
        }

        long unixTime = Instant.now().getEpochSecond();
        long currentBucket = Math.floorDiv(unixTime, passwordPeriod);

        return defaultCodeGenerator.generate(key, currentBucket);
    }

    /**
     * Calculate the remaining seconds of a period with a size of {@link TOTP#passwordPeriod}
     * with the assumption that it has started from the Unix start date
     * @return remain seconds of the period
     */
    public static long getTimePeriodRemainingSeconds() {
        return passwordPeriod - Instant.now().getEpochSecond() % passwordPeriod;

    }
}
