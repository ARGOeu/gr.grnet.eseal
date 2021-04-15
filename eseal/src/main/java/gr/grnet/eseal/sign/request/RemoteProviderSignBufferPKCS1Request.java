package gr.grnet.eseal.sign.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoteProviderSignBufferPKCS1Request extends RemoteProviderSignBufferRequest {

  @JsonProperty("Flags")
  private String flags = "541696";
}