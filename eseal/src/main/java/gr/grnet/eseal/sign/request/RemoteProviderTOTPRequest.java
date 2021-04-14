package gr.grnet.eseal.sign.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoteProviderTOTPRequest {

  @JsonProperty("Username")
  private String username;

  @JsonProperty("Password")
  private String password;

  @JsonProperty("SignPassword")
  private String signPassword;

  @JsonIgnore private String key;

  @JsonIgnore private String url;

  public String toJSON() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(this);
  }
}
