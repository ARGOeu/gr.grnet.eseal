package gr.grnet.eseal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
    * HaricaProperties holds the configuration properties regarding the interaction
    * of the API with the Harica rest api for signing and timestamping documents
 */
@Configuration
@ConfigurationProperties(prefix = "eseal.remote.provider")
public class RemoteProviderProperties {

    /**
     * Remote provider's rest api endpoint
     * this value will map to
     * eseal.remote.provider.endpoint
     */
    private String endpoint;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
