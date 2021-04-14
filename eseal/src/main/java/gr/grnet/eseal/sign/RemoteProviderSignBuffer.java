package gr.grnet.eseal.sign;

import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.sign.request.RemoteProviderSignBufferRequest;
import gr.grnet.eseal.sign.response.RemoteProviderSignBufferResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Component;

/**
 * RemoteProviderSignBuffer extends a {@link RemoteHttpEsealClientAbstract} that allows the usage of
 * a provider's remote http rest api in order to access e-seals and sign digests(buffers)
 */
@Component
public class RemoteProviderSignBuffer
    extends RemoteHttpEsealClientAbstract<
        RemoteProviderSignBufferRequest, RemoteProviderSignBufferResponse> {

  public RemoteProviderSignBuffer(
      RemoteProviderProperties remoteProviderProperties, CloseableHttpClient httpClient) {
    super(httpClient, remoteProviderProperties);
  }
}
