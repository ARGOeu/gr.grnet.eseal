package gr.grnet.eseal.logging;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * The {@link ServiceLogField} holds the service info to be logged.
 */
@Getter
@Setter
public class ServiceLogField extends LogField {

    private String details;

    @Builder
    public ServiceLogField(String details) {
        super(LogType.SERVICE_LOG);
        this.details = details;
    }
}
