package gr.grnet.eseal.sign;

import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.sign.request.RemoteProviderSignDocumentRequest;
import gr.grnet.eseal.sign.response.RemoteProviderSignDocumentResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Component;

/**
 * RemoteProviderHttpEsealClient extends a {@link AbstractRemoteHttpEsealClient} that allows the
 * usage of a provider's remote http rest api in order to access e-seals and
 * executeRemoteProviderRequestResponse documents
 */
@Component
public class RemoteProviderSignDocument
    extends AbstractRemoteHttpEsealClient<
        RemoteProviderSignDocumentRequest, RemoteProviderSignDocumentResponse> {

  public RemoteProviderSignDocument(
      RemoteProviderProperties remoteProviderProperties, CloseableHttpClient httpClient) {
    super(httpClient, remoteProviderProperties);
  }
}
