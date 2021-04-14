package gr.grnet.eseal.sign.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoteProviderSignBufferRequest extends RemoteProviderTOTPRequest {

  @JsonProperty("BufferToSign")
  private String bufferToSign;

  @JsonProperty("Flags")
  private int flags = 545792;
}
