package gr.grnet.eseal.dto;

import gr.grnet.eseal.utils.NotEmptySignDocumentRequestFieldsCheckGroup;
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
  private String bytes;

  @NotEmpty(
      groups = NotEmptySignDocumentRequestFieldsCheckGroup.class,
      message = "Field toSignDocument.name cannot be empty")
  private String name;
}
