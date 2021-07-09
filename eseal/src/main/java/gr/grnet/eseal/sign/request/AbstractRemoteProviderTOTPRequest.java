package gr.grnet.eseal.sign.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.grnet.eseal.utils.TOTP;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractRemoteProviderTOTPRequest extends AbstractRemoteProviderRequest {

  @JsonProperty("SignPassword")
  private String signPassword;

  @Override
  public void setTOTP(String key, int waitForRefreshSeconds) {
    this.signPassword = TOTP.generate(key, waitForRefreshSeconds);
  }
}
