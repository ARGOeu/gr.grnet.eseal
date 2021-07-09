package gr.grnet.eseal.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SignDocumentResponseDto represents a response containing the signed document in base64 encoded.
 */
@Setter
@Getter
@NoArgsConstructor
public class SignDocumentResponseDto {

  private String signedDocumentBytes;

  public SignDocumentResponseDto(final String signedDocumentBytes) {
    this.signedDocumentBytes = signedDocumentBytes;
  }
}
