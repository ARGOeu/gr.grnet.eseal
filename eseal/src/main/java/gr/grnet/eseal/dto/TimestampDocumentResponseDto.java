package gr.grnet.eseal.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TimestampDocumentResponseDto {

  private String timestampedDocumentBytes;

  public TimestampDocumentResponseDto(String timestampedDocumentBytes) {
    this.timestampedDocumentBytes = timestampedDocumentBytes;
  }
}
