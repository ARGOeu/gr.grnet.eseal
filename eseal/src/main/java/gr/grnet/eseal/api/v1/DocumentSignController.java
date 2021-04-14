package gr.grnet.eseal.api.v1;

import gr.grnet.eseal.dto.SignDocumentRequestDto;
import gr.grnet.eseal.dto.SignDocumentResponseDto;
import gr.grnet.eseal.service.SignDocumentService;
import gr.grnet.eseal.utils.NotEmptySignDocumentRequestFieldsCheckGroup;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/signing/")
public class DocumentSignController {

  private final SignDocumentService signDocumentService;

  @Autowired
  public DocumentSignController(SignDocumentService signDocumentService) {
    this.signDocumentService = signDocumentService;
  }

  @PostMapping("/remoteSignDocument")
  public SignDocumentResponseDto signDocument(
      @Validated(NotEmptySignDocumentRequestFieldsCheckGroup.class) @RequestBody
          SignDocumentRequestDto signDocumentRequest) {

    return new SignDocumentResponseDto(
        this.signDocumentService.signDocument(
            signDocumentRequest.getToSignDocument().getBytes(),
            signDocumentRequest.getUsername(),
            signDocumentRequest.getPassword(),
            signDocumentRequest.getKey()));
  }

  @PostMapping("/remoteSignDocumentDetached")
  public SignDocumentResponseDto signDocumentDetached(
      @Validated(NotEmptySignDocumentRequestFieldsCheckGroup.class) @RequestBody
          SignDocumentRequestDto signDocumentRequest) {

    return new SignDocumentResponseDto(
        this.signDocumentService.signDocumentDetached(
            signDocumentRequest.getToSignDocument().getBytes(),
            signDocumentRequest.getUsername(),
            signDocumentRequest.getPassword(),
            signDocumentRequest.getKey(),
            new Date()));
  }
}
