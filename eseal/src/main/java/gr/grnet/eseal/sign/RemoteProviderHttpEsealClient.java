package gr.grnet.eseal.sign;

import static net.logstash.logback.argument.StructuredArguments.f;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.exception.InternalServerErrorException;
import gr.grnet.eseal.exception.InvalidTOTPException;
import gr.grnet.eseal.exception.UnprocessableEntityException;
import gr.grnet.eseal.logging.BackEndLogField;
import gr.grnet.eseal.logging.ServiceLogField;
import gr.grnet.eseal.utils.TOTP;
import gr.grnet.eseal.utils.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.function.Predicate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RemoteProviderHttpEsealClient implements a {@link RemoteHttpEsealClient} that allows the usage of
 * a provider's remote http rest api in order to access e-seals and sign documents
 */
public class RemoteProviderHttpEsealClient implements RemoteHttpEsealClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemoteProviderHttpEsealClient.class);
  private static final String SIGNING_PATH = "dsa/v1/sign";
  private static final String PROTOCOL = "https";
  private static final int SOCKET_TIMEOUT = 30000;
  private static final int CONNECTION_TIMEOUT = 30000;
  private static final int CONNECTION_REQUEST_TIMEOUT = 30000;
  private CloseableHttpClient closeableHttpClient;
  private String signingURL;
  private RemoteProviderProperties remoteProviderProperties;

  public RemoteProviderHttpEsealClient(RemoteProviderProperties remoteProviderProperties)
      throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException,
          KeyManagementException {
    this.remoteProviderProperties = remoteProviderProperties;
    this.signingURL =
        String.format("%s://%s/%s", PROTOCOL, remoteProviderProperties.getEndpoint(), SIGNING_PATH);
    this.closeableHttpClient = buildHttpClient();
  }

  // No args constructor, better used only during testing
  public RemoteProviderHttpEsealClient() {}

  public void setRemoteProviderProperties(RemoteProviderProperties remoteProviderProperties) {
    this.remoteProviderProperties = remoteProviderProperties;
  }

  @Override
  public String sign(String document, String username, String password, String key) {

    // check if retry is enabled
    if (this.remoteProviderProperties.isRetryEnabled()) {
      int retryCount = 0;

      while (retryCount < this.remoteProviderProperties.getRetryCounter()) {

        try {
          return this.doSign(document, username, password, key);
        } catch (InvalidTOTPException | InternalServerErrorException ie) {
          retryCount++;
          LOGGER.warn(
              "Encountered an exception while trying to sign",
              f(ServiceLogField.builder().details(ie.getMessage()).build()));
          LOGGER.info(
              "Retrying for the {} time in {} seconds",
              retryCount,
              this.remoteProviderProperties.getRetryInterval(),
              f(ServiceLogField.builder().build()));
          try {
            Thread.sleep(this.remoteProviderProperties.getRetryInterval() * 1000);
          } catch (InterruptedException e) {
            //
          }
        }
      }
    }
    // if the retry mechanism has been enabled, this is the last retry
    // otherwise is the one and only call to the remote signing service
    return this.doSign(document, username, password, key);
  }

  /**
   * doSign takes care of the internal business logic for connecting to the provider's's remote http
   * api in order to sign the provided document
   */
  private String doSign(String document, String username, String password, String key) {

    // prepare the document signing request
    RemoteProviderSignDocumentRequest remoteProviderSignDocumentRequest =
        new RemoteProviderSignDocumentRequest();
    remoteProviderSignDocumentRequest.fileData = document;
    remoteProviderSignDocumentRequest.username = username;
    remoteProviderSignDocumentRequest.password = password;

    long start = System.currentTimeMillis();

    try {
      // generate new TOTP password
      remoteProviderSignDocumentRequest.signPassword = TOTP.generate(key);

      // TODO
      // Revisit this code block as it has been provided as a temporary solution for the TOTP
      // timeout possibility and we need to re-evaluate it.
      long timePeriodRemainingSeconds = TOTP.getTimePeriodRemainingSeconds();
      if (timePeriodRemainingSeconds
          <= this.remoteProviderProperties.getTotpWaitForRefreshSeconds()) {
        LOGGER.info(
            "TOTP remaining time period is below/at {} seconds, {} seconds.Waiting for expiration.",
            this.remoteProviderProperties.getTotpWaitForRefreshSeconds(),
            timePeriodRemainingSeconds,
            f(ServiceLogField.builder().build()));
        Thread.sleep(timePeriodRemainingSeconds * 1000);
        LOGGER.info("Generating new TOTP", f(ServiceLogField.builder().build()));
        remoteProviderSignDocumentRequest.signPassword = TOTP.generate(key);
      }

      // attempt to sign the document with remote provider
      RemoteProviderSignDocumentResponse remoteProviderSignDocumentResponse =
          this.doPost(remoteProviderSignDocumentRequest);

      String executionTime = Utils.formatTimePeriod(start);

      // check if the signing was successful
      if (!remoteProviderSignDocumentResponse.isSuccessful()) {

        BackEndLogField field =
            BackEndLogField.builder()
                .backendHost(this.remoteProviderProperties.getEndpoint())
                .details(remoteProviderSignDocumentResponse.getErrorData().getMessage())
                .executionTime(executionTime)
                .build();

        signingErrorResponsePredicate(
                "The user is locked",
                field,
                (r) -> r.getErrorData().getMessage().contains("The user is locked"),
                new InternalServerErrorException("The user is locked and cannot logon"))
            .or(
                signingErrorResponsePredicate(
                    "Failed to login",
                    field,
                    (r) -> r.getErrorData().getMessage().contains("Failed to Logon"),
                    new UnprocessableEntityException("Wrong user credentials")))
            .or(
                signingErrorResponsePredicate(
                    "Invalid TOTP",
                    field,
                    (r) -> r.getErrorData().getMessage().contains("Failed to Sign"),
                    new InvalidTOTPException()))
            .or(
                signingErrorResponsePredicate(
                    "Error response from provider",
                    field,
                    (r) -> true,
                    new InternalServerErrorException("Error with signing backend")))
            .test(remoteProviderSignDocumentResponse);
      }

      // returned the signed document
      LOGGER.info(
          "Successful document signing",
          f(
              BackEndLogField.builder()
                  .backendHost(this.remoteProviderProperties.getEndpoint())
                  .executionTime(executionTime)
                  .build()));

      return remoteProviderSignDocumentResponse.getSignedFileData();
    } catch (CodeGenerationException e) {
      LOGGER.error(
          "TOTP generator has encountered an error",
          f(ServiceLogField.builder().details(e.getMessage()).build()));
      throw new InternalServerErrorException("TOTP generator has encountered an error");
    } catch (IOException ioe) {

      LOGGER.error(
          "Error communicating with provider's backend",
          f(
              BackEndLogField.builder()
                  .backendHost(this.remoteProviderProperties.getEndpoint())
                  .details(ioe.getMessage())
                  .executionTime(Utils.formatTimePeriod(start))
                  .build()));
      throw new InternalServerErrorException("Signing backend unavailable");
    } catch (InterruptedException ie) {
      LOGGER.error(
          "Internal thread error", f(ServiceLogField.builder().details(ie.getMessage()).build()));
      throw new InternalServerErrorException("Internal thread error");
    }
  }

  private RemoteProviderSignDocumentResponse doPost(
      RemoteProviderSignDocumentRequest remoteProviderSignDocumentRequest) throws IOException {

    StringEntity postBody = new StringEntity(remoteProviderSignDocumentRequest.toJSON());
    postBody.setContentType("application/json");

    // Set up a post request
    HttpPost postReq = new HttpPost(this.signingURL);
    postReq.setEntity(postBody);
    CloseableHttpResponse response = this.closeableHttpClient.execute(postReq);
    HttpEntity entity = response.getEntity();

    // Read the response
    String line;
    BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
    StringBuilder currentMsg = new StringBuilder();
    while ((line = br.readLine()) != null) {
      currentMsg.append(line);
    }

    // Make sure that the interaction with the service has closed
    EntityUtils.consume(entity);
    response.close();
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(currentMsg.toString(), RemoteProviderSignDocumentResponse.class);
  }

  private CloseableHttpClient buildHttpClient()
      throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
          KeyManagementException {

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

  private Predicate<RemoteProviderSignDocumentResponse> signingErrorResponsePredicate(
      String message,
      BackEndLogField field,
      Predicate<? super RemoteProviderSignDocumentResponse> predicate,
      RuntimeException exc) {
    return t -> {
      boolean r = predicate.test(t);
      if (r) {
        LOGGER.error(message, f(field));
        throw exc;
      }
      return r;
    };
  }

  @Setter
  @Getter
  @NoArgsConstructor
  private class RemoteProviderSignDocumentRequest {
    @JsonProperty("Username")
    private String username;

    @JsonProperty("Password")
    private String password;

    @JsonProperty("SignPassword")
    private String signPassword;

    @JsonProperty("FileData")
    private String fileData;

    @JsonProperty("FileType")
    private String fileType = "pdf";

    @JsonProperty("Page")
    private int page = 0;

    @JsonProperty("Height")
    private int height = 100;

    @JsonProperty("Width")
    private int width = 100;

    @JsonProperty("X")
    private int xx = 140;

    @JsonProperty("Y")
    private int yy = 230;

    @JsonProperty("Appearance")
    private int appearance = 15;

    private String toJSON() throws JsonProcessingException {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(this);
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  private static class RemoteProviderSignDocumentResponse {
    @JsonProperty("Success")
    private Boolean success;

    @JsonProperty("Data")
    private DataField data;

    @JsonProperty("ErrData")
    private ErrorData errorData;

    private Boolean isSuccessful() {
      return this.success;
    }

    private String getSignedFileData() {
      return this.data.signedFileData;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  private static class DataField {
    @JsonProperty("SignedFileData")
    private String signedFileData;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  private static class ErrorData {
    @JsonProperty("Message")
    private String message;

    @JsonProperty("Module")
    private Object module;

    @JsonProperty("Code")
    private int code;

    @JsonProperty("InnerCode")
    private int innerCode;
  }
}
