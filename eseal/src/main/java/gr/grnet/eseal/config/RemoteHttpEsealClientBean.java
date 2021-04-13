package gr.grnet.eseal.config;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RemoteHttpEsealClientBean is a {@link Bean} responsible for the exposure of the http client that
 * will take care of signing and timestamping documents, using a remote provider
 */
@Configuration
public class RemoteHttpEsealClientBean {

  private final RemoteProviderProperties remoteProviderProperties;
  private static final int SOCKET_TIMEOUT = 30000;
  private static final int CONNECTION_TIMEOUT = 30000;
  private static final int CONNECTION_REQUEST_TIMEOUT = 30000;

  @Autowired
  public RemoteHttpEsealClientBean(RemoteProviderProperties remoteProviderProperties) {
    this.remoteProviderProperties = remoteProviderProperties;
  }

  @Bean
  public CloseableHttpClient httpClient()
      throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, IOException,
          CertificateException {
    // socket config
    SocketConfig socketCfg = SocketConfig.custom().setSoTimeout(SOCKET_TIMEOUT).build();

    RequestConfig reqCfg =
        RequestConfig.custom()
            .setConnectTimeout(CONNECTION_TIMEOUT)
            .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
            .build();

    // ssl context
    SSLContext sslContext = SSLContext.getInstance("SSL");

    // set up a TrustManager that trusts everything
    if (!this.remoteProviderProperties.isTlsVerifyEnabled()) {
      sslContext.init(
          null,
          new TrustManager[] {
            new X509TrustManager() {
              public X509Certificate[] getAcceptedIssuers() {
                return null;
              }

              public void checkClientTrusted(X509Certificate[] certs, String authType) {}

              public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }
          },
          new SecureRandom());
    } else {

      // set up a trust manager with the appropriate client truststore
      // in order to verify the remote provider api

      /* Load client truststore. */
      KeyStore theClientTruststore =
          KeyStore.getInstance(this.remoteProviderProperties.getTruststoreType());

      InputStream clientTruststoreIS =
          this.getClass()
              .getResourceAsStream("/".concat(this.remoteProviderProperties.getTruststoreFile()));

      theClientTruststore.load(
          clientTruststoreIS, this.remoteProviderProperties.getTruststorePassword().toCharArray());

      /* Create a trust manager factory using the client truststore. */
      final TrustManagerFactory theTrustManagerFactory =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      theTrustManagerFactory.init(theClientTruststore);

      /*
       * Create a SSL context with a trust manager that uses the
       * client truststore.
       */
      sslContext.init(null, theTrustManagerFactory.getTrustManagers(), new SecureRandom());
    }

    // build the client
    return HttpClients.custom()
        .setSSLContext(sslContext)
        .setDefaultRequestConfig(reqCfg)
        .setDefaultSocketConfig(socketCfg)
        .build();
  }
}
