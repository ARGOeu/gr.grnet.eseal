package gr.grnet.eseal.sign;

import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.sign.request.RemoteProviderSignDocumentRequest;
import gr.grnet.eseal.sign.response.RemoteProviderSignDocumentResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Component;

/**
 * RemoteProviderHttpEsealClient extends a {@link RemoteHttpEsealClientAbstract} that allows the
 * usage of a provider's remote http rest api in order to access e-seals and sign documents
 */
@Component
public class RemoteProviderSignDocument
    extends RemoteHttpEsealClientAbstract<
        RemoteProviderSignDocumentRequest, RemoteProviderSignDocumentResponse> {

  public RemoteProviderSignDocument(
      RemoteProviderProperties remoteProviderProperties, CloseableHttpClient httpClient) {
    super(httpClient, remoteProviderProperties);
  }
}
