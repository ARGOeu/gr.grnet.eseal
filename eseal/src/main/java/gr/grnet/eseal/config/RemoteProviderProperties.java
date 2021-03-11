package gr.grnet.eseal.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
    * RemoteProviderProperties holds the configuration properties regarding the interaction
    * of the API with the Provider's rest api for signing and timestamping documents
 */
@Setter
@Getter
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "eseal.remote.provider")
public class RemoteProviderProperties {

    /**
     * Remote provider's rest api endpoint
     * this value will map to
     * eseal.remote.provider.endpoint
     */
    private String endpoint;

    /**
     * Retry requests on the remove provider
     * if an error occurs
     */
    @Value("${eseal.remote.provider.retry.enabled}")
    private boolean retryEnabled;

    /**
     * Request retry counter
     */
    @Value("${eseal.remote.provider.retry.counter}")
    private int retryCounter;

    /**
     * Time in seconds between retry attempts
     */
    @Value("${eseal.remote.provider.retry.interval}")
    private int retryInterval;

    /**
     * Time in seconds that indicate for what time values should we wait for a new token to
     * get generated rather than using the already created one which near expiration
     */
    @Value("${eseal.remote.provider.totp.refresh.seconds.wait}")
    private int totpWaitForRefreshSeconds;

    /**
     * Try to verify the remote API certs using a client truststore,
     * otherwise use a TRUST_ALL strategy
     */
    @Value("${eseal.remote.provider.tls.verify}")
    private boolean tlsVerifyEnabled;

    /**
     * Keystore file containing the needed certs to verify the remote API(client truststore)
     */
    @Value("${eseal.remote.provider.truststore.file}")
    private String truststoreFile;

    /**
     * Password to access the client truststore
     */
    @Value("${eseal.remote.provider.truststore.password}")
    private String truststorePassword;

    /**
     * Type of the client truststore
     */
    @Value("${eseal.remote.provider.truststore.type}")
    private String truststoreType;

}
