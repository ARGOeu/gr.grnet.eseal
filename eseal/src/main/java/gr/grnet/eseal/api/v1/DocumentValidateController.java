package gr.grnet.eseal.api.v1;

import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;
import gr.grnet.eseal.dto.ValidateDocumentRequestDto;
import gr.grnet.eseal.service.ValidateDocumentService;
import gr.grnet.eseal.utils.validation.Base64RequestFieldCheckGroup;
import gr.grnet.eseal.utils.validation.NotEmptyValidateDocumentRequestFieldsCheckGroup;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/validation/")
public class DocumentValidateController {

  private final ValidateDocumentService validateDocumentService;

  @Autowired
  public DocumentValidateController(ValidateDocumentService validateDocumentService) {
    this.validateDocumentService = validateDocumentService;
  }

  @PostMapping("/validateDocument")
  public WSReportsDTO validateDocument(
      @Validated(
              value = {
                NotEmptyValidateDocumentRequestFieldsCheckGroup.class,
                Base64RequestFieldCheckGroup.class
              })
          @RequestBody
          ValidateDocumentRequestDto validateDocumentRequestDto,
      HttpSession session) {

    session.setAttribute("document_name", validateDocumentRequestDto.getSignedDocument().getName());

    return this.validateDocumentService.validateDocument(
        validateDocumentRequestDto.getSignedDocument().getBytes(),
        validateDocumentRequestDto.getSignedDocument().getName());
  }
}
