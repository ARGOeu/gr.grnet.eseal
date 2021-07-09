package gr.grnet.eseal.sign.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class RemoteProviderSignBufferResponse extends RemoteProviderTOTPResponse {

  @JsonProperty("Data")
  private SignatureField data;

  public String getSignature() {
    return this.data.signature;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class SignatureField {
    @JsonProperty("Signature")
    private String signature;
  }
}
