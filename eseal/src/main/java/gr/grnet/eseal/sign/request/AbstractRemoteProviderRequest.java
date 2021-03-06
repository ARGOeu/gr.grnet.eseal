package gr.grnet.eseal.sign.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractRemoteProviderRequest {

  @JsonProperty("Username")
  private String username;

  @JsonProperty("Password")
  private String password;

  @JsonIgnore private String key;

  @JsonIgnore private String url;

  @JsonIgnore private String description;

  public void setTOTP(String key, int waitForRefreshSeconds) {}

  public String toJSON() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(this);
  }
}
