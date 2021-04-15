package gr.grnet.eseal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.grnet.eseal.enums.TSASourceEnum;
import gr.grnet.eseal.utils.ValueOfEnum;
import gr.grnet.eseal.utils.validation.Base64;
import gr.grnet.eseal.utils.validation.Base64RequestFieldCheckGroup;
import gr.grnet.eseal.utils.validation.NotEmptyTimestampDocumentRequestFieldsCheckGroup;
import gr.grnet.eseal.utils.validation.ValueOfEnumRequestFieldCheckGroup;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TimestampDocumentRequestDto {

  @NotNull(
      groups = NotEmptyTimestampDocumentRequestFieldsCheckGroup.class,
      message = "Field toTimestampDocument cannot be empty")
  @JsonProperty("toTimestampDocument")
  @Valid
  private TimestampDocumentRequestDto.ToTimestampDocument toTimestampDocument;

  @ValueOfEnum(
      groups = ValueOfEnumRequestFieldCheckGroup.class,
      enumClass = TSASourceEnum.class,
      message = "Possible values of property tsaSource are [APED, HARICA]")
  @JsonProperty("tsaSource")
  private String tsaSource = TSASourceEnum.HARICA.name();

  @Getter
  @Setter
  @NoArgsConstructor
  public static class ToTimestampDocument {

    @NotEmpty(
        groups = NotEmptyTimestampDocumentRequestFieldsCheckGroup.class,
        message = "Field toTimestampDocument.bytes cannot be empty")
    @Base64(
        groups = Base64RequestFieldCheckGroup.class,
        message = "Field toTimestampDocument.bytes should be encoded in base64 format")
    private String bytes;

    @NotEmpty(
        groups = NotEmptyTimestampDocumentRequestFieldsCheckGroup.class,
        message = "Field toTimestampDocument.name cannot be empty")
    private String name;
  }
}
