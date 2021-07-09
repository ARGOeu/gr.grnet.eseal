package gr.grnet.eseal.sign.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoteProviderSignBufferPKCS1Request extends AbstractRemoteProviderSignBufferRequest {

  @JsonProperty("Flags")
  private String flags = "541696";

  @JsonIgnore private String description = "Remote Provider Sign Buffer PKCS1 Request";
}
