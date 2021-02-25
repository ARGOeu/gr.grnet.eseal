package gr.grnet.eseal.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * The {@link RequestLogField} holds the request info to be logged.
 */
@Getter
@Setter
public class RequestLogField extends LogField {

    private String method;
    private String path;
    private String processing_time;
    private String status;


    @Builder
    public RequestLogField(String type, String processing_time, String method, String path, String status) {
        super(type);
        this.processing_time = processing_time;
        this.method = method;
        this.path = path;
        this.status = status;
    }
}
