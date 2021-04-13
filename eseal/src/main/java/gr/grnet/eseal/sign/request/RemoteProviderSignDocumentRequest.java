package gr.grnet.eseal.sign.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RemoteProviderSignDocumentRequest extends RemoteProviderTOTPRequest {
  @JsonProperty("Username")
  private String username;

  @JsonProperty("Password")
  private String password;

  @JsonProperty("FileData")
  private String fileData;

  @JsonProperty("FileType")
  private String fileType = "pdf";

  @JsonProperty("Page")
  private int page = 0;

  @JsonProperty("Height")
  private int height = 100;

  @JsonProperty("Width")
  private int width = 100;

  @JsonProperty("X")
  private int xx = 140;

  @JsonProperty("Y")
  private int yy = 230;

  @JsonProperty("Appearance")
  private int appearance = 15;
}
