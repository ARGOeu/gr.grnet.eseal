package gr.grnet.eseal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.grnet.eseal.config.tsp.TSPSourceEnum;
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
      enumClass = TSPSourceEnum.class,
      message = "Possible values of property tspSource are [APED, HARICA]")
  @JsonProperty("tspSource")
  private String tspSource = "APED";

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
