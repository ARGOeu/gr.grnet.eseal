package gr.grnet.eseal.sign.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractRemoteProviderSignBufferRequest
    extends AbstractRemoteProviderTOTPRequest {

  @JsonProperty("BufferToSign")
  private String bufferToSign;
}
