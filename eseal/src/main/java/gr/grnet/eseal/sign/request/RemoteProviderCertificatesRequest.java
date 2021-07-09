package gr.grnet.eseal.sign.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoteProviderCertificatesRequest extends AbstractRemoteProviderRequest {

  @JsonIgnore private String description = "Remote Provider Certificates Request";
}
