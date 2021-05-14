package gr.grnet.eseal.logging;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.grnet.eseal.enums.LogType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** The {@link RequestLogField} holds the request info to be logged. */
@Getter
@Setter
public class RequestLogField extends LogField {

  private String method;
  private String path;

  @JsonProperty("processing_time")
  private String processingTime;

  private String status;

  @Builder
  public RequestLogField(String processingTime, String method, String path, String status) {
    super(LogType.REQUEST_LOG);
    this.processingTime = processingTime;
    this.method = method;
    this.path = path;
    this.status = status;
  }
}
