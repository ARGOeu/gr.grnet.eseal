package gr.grnet.eseal.sign.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractRemoteProviderResponse {

  @JsonProperty("Success")
  private Boolean success = true;

  public abstract String getErrorMessage();
}
