package gr.grnet.eseal.logging;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * The {@link BackEndLogField} holds the backend info to be logged.
 */
@Getter
@Setter
public class BackEndLogField extends LogField {

    @JsonProperty("backend_host")
    private String backendHost;
    @JsonProperty("execution_time")
    private String executionTime;
    private String details;

    @Builder
    public BackEndLogField(String backendHost, String details, String executionTime) {
        super("backend_log");
        this.backendHost = backendHost;
        this.details = details;
        this.executionTime = executionTime;
    }
}
