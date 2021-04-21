package gr.grnet.eseal.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignDocumentDetachedRequestDto extends SignDocumentRequestDto {
  private String imageBytes = "";
}
