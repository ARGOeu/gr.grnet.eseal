package gr.grnet.eseal.config;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
    * RemoteProviderProperties holds the configuration properties regarding the interaction
    * of the API with the Harica rest api for signing and timestamping documents
 */
@Setter
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

    public String getEndpoint() {
        return endpoint;
    }

    public boolean isRetryEnabled() {
        return retryEnabled;
    }

    public int getRetryCounter() {
        return retryCounter;
    }

    public int getRetryInterval() {
        return retryInterval;
    }

    public int getTotpWaitForRefreshSeconds() {
        return totpWaitForRefreshSeconds;
    }
}
