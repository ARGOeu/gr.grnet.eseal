package gr.grnet.eseal.service;

import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.ws.dto.RemoteDocument;
import eu.europa.esig.dss.ws.validation.common.RemoteDocumentValidationService;
import eu.europa.esig.dss.ws.validation.dto.DataToValidateDTO;
import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;
import gr.grnet.eseal.validation.DocumentValidatorLOTL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidateDocumentService {

  private RemoteDocumentValidationService remoteDocumentValidationService;

  @Autowired
  public ValidateDocumentService(DocumentValidatorLOTL lotlValidator) {
    this.remoteDocumentValidationService = new RemoteDocumentValidationService();
    this.remoteDocumentValidationService.setVerifier(lotlValidator.getCertificateVerifier());
  }

  public WSReportsDTO validateDocument(String documentBytes, String documentName) {

    DataToValidateDTO dataToValidateDTO = new DataToValidateDTO();

    dataToValidateDTO.setSignedDocument(
        new RemoteDocument(Utils.fromBase64(documentBytes), documentName));
    return this.remoteDocumentValidationService.validateDocument(dataToValidateDTO);
  }
}
