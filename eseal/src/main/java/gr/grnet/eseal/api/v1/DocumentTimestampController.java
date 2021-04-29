package gr.grnet.eseal.api.v1;

import gr.grnet.eseal.config.tsp.TSPSourceEnum;
import gr.grnet.eseal.dto.TimestampDocumentRequestDto;
import gr.grnet.eseal.dto.TimestampDocumentResponseDto;
import gr.grnet.eseal.service.TimestampDocumentService;
import javax.validation.Valid;
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
      @Valid @RequestBody TimestampDocumentRequestDto timestampDocumentRequestDto) {

    return new TimestampDocumentResponseDto(
        timestampDocumentService.timestampDocument(
            timestampDocumentRequestDto.getToTimestampDocument().getBytes(),
            TSPSourceEnum.valueOf(timestampDocumentRequestDto.getTspSource())));
  }
}
