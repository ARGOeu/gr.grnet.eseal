package gr.grnet.eseal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.grnet.eseal.timestamp.TSASourceEnum;
import gr.grnet.eseal.utils.ValueOfEnum;
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

  @NotNull(message = "Field toTimestampDocument cannot be empty")
  @JsonProperty("toTimestampDocument")
  @Valid
  private TimestampDocumentRequestDto.ToTimestampDocument toTimestampDocument;

  @ValueOfEnum(
      enumClass = TSASourceEnum.class,
      message = "Possible values of property tsaSource are [APED, HARICA]")
  @JsonProperty("tsaSource")
  private String tsaSource = TSASourceEnum.HARICA.name();

  @Getter
  @Setter
  @NoArgsConstructor
  public static class ToTimestampDocument {

    @NotEmpty(message = "Field toTimestampDocument.bytes cannot be empty")
    private String bytes;

    @NotEmpty(message = "Field toTimestampDocument.name cannot be empty")
    private String name;
  }
}
