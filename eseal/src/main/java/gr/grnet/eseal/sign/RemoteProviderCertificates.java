package gr.grnet.eseal.sign;

import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.sign.request.RemoteProviderCertificatesRequest;
import gr.grnet.eseal.sign.response.RemoteProviderCertificatesResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Component;

/**
 * RemoteProviderCertificates extends a {@link AbstractRemoteHttpEsealClient} that allows the usage
 * of a provider's remote http rest api in order to access an e-seal certificates
 */
@Component
public class RemoteProviderCertificates
    extends AbstractRemoteHttpEsealClient<
        RemoteProviderCertificatesRequest, RemoteProviderCertificatesResponse> {

  public RemoteProviderCertificates(
      RemoteProviderProperties remoteProviderProperties, CloseableHttpClient httpClient) {
    super(httpClient, remoteProviderProperties);
  }
}
