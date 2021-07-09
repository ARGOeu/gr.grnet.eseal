package gr.grnet.eseal.dto;

import gr.grnet.eseal.utils.validation.Base64;
import gr.grnet.eseal.utils.validation.Base64RequestFieldCheckGroup;
import gr.grnet.eseal.utils.validation.NotEmptyValidateDocumentRequestFieldsCheckGroup;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignedDocument {

  @NotEmpty(
      groups = NotEmptyValidateDocumentRequestFieldsCheckGroup.class,
      message = "Field signedDocument.bytes cannot be empty")
  @Base64(
      groups = Base64RequestFieldCheckGroup.class,
      message = "Field toSignDocument.bytes should be encoded in base64 format")
  private String bytes;

  @NotEmpty(
      groups = NotEmptyValidateDocumentRequestFieldsCheckGroup.class,
      message = "Field signedDocument.name cannot be empty")
  private String name;
}
