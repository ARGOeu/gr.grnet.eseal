package gr.grnet.eseal.sign.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoteProviderSignDocumentResponse extends RemoteProviderTOTPResponse {

  @JsonProperty("Data")
  private DataField data;

  public String getSignedFileData() {
    return this.data.signedFileData;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class DataField {
    @JsonProperty("SignedFileData")
    private String signedFileData;
  }
}
