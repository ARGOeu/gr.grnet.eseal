package gr.grnet.eseal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
    * HaricaProperties holds the configuration properties regarding the interaction
    * of the API with the Harica rest api for signing and timestamping documents
 */
@Configuration
@ConfigurationProperties(prefix = "eseal.harica")
public class HaricaProperties {

    /**
     * Harica rest api endpoint
     * this value will map to
     * eseal.harica.endpoint
     */
    private String endpoint;


    /**
     * Harica rest api access username
     * this value will map to
     * eseal.harica.username
     */
    private String username;

    /**
     * Harica rest api access password
     * this value will map to
     * eseal.harica.password
     */
    private String password;

    /**
     * Harica rest api signing TOTP key
     * this value will map to
     * eseal.harica.key
     */
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
