package gr.grnet.eseal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.grnet.eseal.utils.NotEmptySignDocumentRequestFieldsCheckGroup;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** SignDocumentRequestDto represents an incoming signing request. */
@Setter
@Getter
@NoArgsConstructor
public class SignDocumentRequestDto {

  @NotEmpty(
      groups = NotEmptySignDocumentRequestFieldsCheckGroup.class,
      message = "Field username cannot be empty")
  private String username;

  @NotEmpty(
      groups = NotEmptySignDocumentRequestFieldsCheckGroup.class,
      message = "Field password cannot be empty")
  private String password;

  @NotEmpty(
      groups = NotEmptySignDocumentRequestFieldsCheckGroup.class,
      message = "Field key cannot be empty")
  private String key;

  @NotNull(
      groups = NotEmptySignDocumentRequestFieldsCheckGroup.class,
      message = "Field toSignDocument cannot be empty")
  @JsonProperty("toSignDocument")
  @Valid
  private ToSignDocument toSignDocument;

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
}
