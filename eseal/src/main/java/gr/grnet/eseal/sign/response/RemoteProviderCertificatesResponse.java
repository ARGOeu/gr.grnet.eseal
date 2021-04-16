package gr.grnet.eseal.sign.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoteProviderCertificatesResponse extends AbstractRemoteProviderResponse {

  @JsonProperty("Issuer")
  private String issuer;

  @JsonProperty("Subject")
  private String subject;

  @JsonProperty("NotBefore")
  private String notBefore;

  @JsonProperty("NotAfter")
  private String notAfter;

  @JsonProperty("SerialNumber")
  private String serialNumber;

  @JsonProperty("Thumbprint")
  private String thumbprint;

  @JsonProperty("Certificates")
  private String[] certificates;

  @JsonProperty("Message")
  private String message;

  @Override
  public String getErrorMessage() {
    return this.message;
  }
}
