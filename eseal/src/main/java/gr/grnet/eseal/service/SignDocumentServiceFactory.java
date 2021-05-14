package gr.grnet.eseal.service;

import gr.grnet.eseal.enums.Sign;
import gr.grnet.eseal.exception.InternalServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignDocumentServiceFactory {

  private final RemoteSignDocumentServicePKCS7 remoteSignDocumentServicePKCS7;
  private final RemoteSignDocumentService remoteSignDocumentService;
  private final RemoteSignDocumentServicePKCS1 remoteSignDocumentServicePKCS1;

  @Autowired
  public SignDocumentServiceFactory(
      RemoteSignDocumentServicePKCS7 remoteSignDocumentServicePKCS7,
      RemoteSignDocumentService remoteSignDocumentService,
      RemoteSignDocumentServicePKCS1 remoteSignDocumentServicePKCS1) {
    this.remoteSignDocumentServicePKCS7 = remoteSignDocumentServicePKCS7;
    this.remoteSignDocumentService = remoteSignDocumentService;
    this.remoteSignDocumentServicePKCS1 = remoteSignDocumentServicePKCS1;
  }

  public SignDocumentService create(Sign sign) {

    switch (sign) {
      case REMOTE_SIGN:
        return remoteSignDocumentService;
      case PKCS7:
        return remoteSignDocumentServicePKCS7;
      case PKCS1:
        return remoteSignDocumentServicePKCS1;
      default:
        throw new InternalServerErrorException("Unable to sign the document");
    }
  }
}
