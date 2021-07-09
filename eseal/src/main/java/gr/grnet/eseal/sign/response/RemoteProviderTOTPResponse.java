package gr.grnet.eseal.sign.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class RemoteProviderTOTPResponse extends AbstractRemoteProviderResponse {

  @JsonProperty("ErrData")
  private ErrorData errorData;

  @Override
  public String getErrorMessage() {
    return this.errorData.getMessage();
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class ErrorData {
    @JsonProperty("Message")
    private String message;

    @JsonProperty("Module")
    private Object module;

    @JsonProperty("Code")
    private int code;

    @JsonProperty("InnerCode")
    private int innerCode;
  }
}
