package gr.grnet.eseal.api.v1;

import gr.grnet.eseal.dto.TimestampDocumentRequestDto;
import gr.grnet.eseal.dto.TimestampDocumentResponseDto;
import gr.grnet.eseal.enums.TSASourceEnum;
import gr.grnet.eseal.service.TimestampDocumentService;
import gr.grnet.eseal.utils.validation.Base64RequestFieldCheckGroup;
import gr.grnet.eseal.utils.validation.NotEmptyTimestampDocumentRequestFieldsCheckGroup;
import gr.grnet.eseal.utils.validation.ValueOfEnumRequestFieldCheckGroup;
import javax.servlet.http.HttpSession;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/timestamping")
public class DocumentTimestampController {

  private final TimestampDocumentService timestampDocumentService;

  public DocumentTimestampController(TimestampDocumentService timestampDocumentService) {
    this.timestampDocumentService = timestampDocumentService;
  }

  @PostMapping("/remoteTimestampDocument")
  public TimestampDocumentResponseDto timestampDocument(
      @Validated(
              value = {
                NotEmptyTimestampDocumentRequestFieldsCheckGroup.class,
                Base64RequestFieldCheckGroup.class,
                ValueOfEnumRequestFieldCheckGroup.class
              })
          @RequestBody
          TimestampDocumentRequestDto timestampDocumentRequestDto,
      HttpSession session) {

    session.setAttribute(
        "document_name", timestampDocumentRequestDto.getToTimestampDocument().getName());

    return new TimestampDocumentResponseDto(
        timestampDocumentService.timestampDocument(
            timestampDocumentRequestDto.getToTimestampDocument().getBytes(),
            TSASourceEnum.valueOf(timestampDocumentRequestDto.getTsaSource())));
  }
}
