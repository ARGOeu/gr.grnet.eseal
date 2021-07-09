package gr.grnet.eseal.dto;

import gr.grnet.eseal.utils.validation.Base64;
import gr.grnet.eseal.utils.validation.Base64RequestFieldCheckGroup;
import gr.grnet.eseal.utils.validation.NotEmptySignDocumentRequestFieldsCheckGroup;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ToSignDocument {

  @NotEmpty(
      groups = NotEmptySignDocumentRequestFieldsCheckGroup.class,
      message = "Field toSignDocument.bytes cannot be empty")
  @Base64(
      groups = Base64RequestFieldCheckGroup.class,
      message = "Field toSignDocument.bytes should be encoded in base64 format")
  private String bytes;

  @NotEmpty(
      groups = NotEmptySignDocumentRequestFieldsCheckGroup.class,
      message = "Field toSignDocument.name cannot be empty")
  private String name;
}
