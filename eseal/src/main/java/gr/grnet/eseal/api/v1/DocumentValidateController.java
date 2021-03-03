package gr.grnet.eseal.api.v1;

import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;
import gr.grnet.eseal.dto.ValidateDocumentRequestDto;
import gr.grnet.eseal.service.ValidateDocumentService;
import gr.grnet.eseal.utils.NotEmptyValidateDocumentRequestFieldsCheckGroup;
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
    public WSReportsDTO validateDocument(@Validated(NotEmptyValidateDocumentRequestFieldsCheckGroup.class)
                                         @RequestBody ValidateDocumentRequestDto validateDocumentRequestDto) {

        return this.validateDocumentService.validateDocument(
                validateDocumentRequestDto.getSignedDocument().getBytes(),
                validateDocumentRequestDto.getSignedDocument().getName());
    }
}
