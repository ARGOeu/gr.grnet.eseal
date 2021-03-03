package gr.grnet.eseal.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * ValidationProperties holds all configuration information needed
 * for the initialization of the {@link gr.grnet.eseal.validation.DocumentValidatorLOTL}
 */
@Getter
@Setter
@NoArgsConstructor
@Configuration
public class ValidationProperties {

    /**
     * Keystore to be used to access the lotl source
     */
    @Value("${eseal.validation.oj.keystore.filename}")
    private String officialJournalKeystoreFile;

    /**
     * Official journal keystore type
     */
    @Value("${eseal.validation.oj.keystore.type}")
    private String officialJournalKeystoreType;

    /**
     * Official journal keystore password
     */
    @Value("${eseal.validation.oj.keystore.password}")
    private String officialJournalKeystorePassword;

    /**
     *  Official journal url
     */
    @Value("${eseal.validation.oj.url}")
    private String officialJournalUrl;

    /**
     * European lotl url
     */
    @Value("${eseal.validation.lotl.url}")
    private String lotlUrl;

    /**
     * LOTL trust sources file system cache directory
     */
    @Value("${eseal.validation.lotl.cache.dir}")
    private String lotlCacheDir;
}
