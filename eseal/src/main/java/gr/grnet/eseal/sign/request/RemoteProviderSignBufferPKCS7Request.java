package gr.grnet.eseal.sign.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoteProviderSignBufferPKCS7Request extends AbstractRemoteProviderSignBufferRequest {

  @JsonProperty("Flags")
  private String flags = "544792";

  @JsonIgnore private String description = "Remote Provider Sign Buffer PKCS7 Request";
}
