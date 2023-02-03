package gr.grnet.eseal.dto;

import gr.grnet.eseal.enums.VisibleSignatureText;
import gr.grnet.eseal.utils.ValueOfEnum;
import gr.grnet.eseal.utils.validation.ValueOfEnumRequestFieldCheckGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignDocumentDetachedRequestDto extends SignDocumentRequestDto {
  private String imageBytes = "";

  private Boolean imageVisibility = true;

  @ValueOfEnum(
      groups = ValueOfEnumRequestFieldCheckGroup.class,
      enumClass = VisibleSignatureText.class,
      message =
          "Possible values of property visibleSignatureText are [CN, OU, CN_OU, STATIC, TEXT]")
  private String visibleSignatureText = VisibleSignatureText.STATIC.name();
}
