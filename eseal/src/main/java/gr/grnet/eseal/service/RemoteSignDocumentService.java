package gr.grnet.eseal.service;

import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.dto.SignDocumentDto;
import gr.grnet.eseal.enums.Path;
import gr.grnet.eseal.sign.RemoteProviderSignDocument;
import gr.grnet.eseal.sign.request.RemoteProviderSignDocumentRequest;
import gr.grnet.eseal.sign.response.RemoteProviderSignDocumentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "remoteSignDocumentService")
public class RemoteSignDocumentService implements SignDocumentService {

  private final RemoteProviderProperties remoteProviderProperties;

  public void setRemoteProviderSignDocument(RemoteProviderSignDocument remoteProviderSignDocument) {
    this.remoteProviderSignDocument = remoteProviderSignDocument;
  }

  private RemoteProviderSignDocument remoteProviderSignDocument;

  @Autowired
  public RemoteSignDocumentService(
      RemoteProviderProperties remoteProviderProperties,
      RemoteProviderSignDocument remoteProviderSignDocument) {
    this.remoteProviderProperties = remoteProviderProperties;
    this.remoteProviderSignDocument = remoteProviderSignDocument;
  }

  @Override
  public String signDocument(SignDocumentDto signDocumentDto) {
    RemoteProviderSignDocumentRequest request = new RemoteProviderSignDocumentRequest();
    request.setKey(signDocumentDto.getKey());
    request.setFileData(signDocumentDto.getBytes());
    request.setUsername(signDocumentDto.getUsername());
    request.setPassword(signDocumentDto.getPassword());
    request.setUrl(
        String.format(
            "%s://%s/%s", "https", remoteProviderProperties.getEndpoint(), Path.REMOTE_SIGNING));

    RemoteProviderSignDocumentResponse response =
        remoteProviderSignDocument.executeRemoteProviderRequestResponse(
            request,
            RemoteProviderSignDocumentResponse.class,
            SignDocumentService.errorResponseFunction());

    return response.getSignedFileData();
  }
}
