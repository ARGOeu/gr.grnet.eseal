package gr.grnet.eseal.sign;

import static net.logstash.logback.argument.StructuredArguments.f;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.exception.InternalServerErrorException;
import gr.grnet.eseal.exception.InvalidTOTPException;
import gr.grnet.eseal.logging.BackEndLogField;
import gr.grnet.eseal.logging.ServiceLogField;
import gr.grnet.eseal.sign.request.AbstractRemoteProviderRequest;
import gr.grnet.eseal.sign.response.AbstractRemoteProviderResponse;
import gr.grnet.eseal.utils.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link RemoteHttpEsealClient} to provide methods to
 * executeRemoteProviderRequestResponse the content in {@link Request}.
 */
public abstract class AbstractRemoteHttpEsealClient<
        Request extends AbstractRemoteProviderRequest,
        Response extends AbstractRemoteProviderResponse>
    implements RemoteHttpEsealClient<Request, Response> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRemoteHttpEsealClient.class);

  private final CloseableHttpClient closeableHttpClient;
  private final RemoteProviderProperties remoteProviderProperties;

  protected AbstractRemoteHttpEsealClient(
      CloseableHttpClient closeableHttpClient, RemoteProviderProperties remoteProviderProperties) {
    this.closeableHttpClient = closeableHttpClient;
    this.remoteProviderProperties = remoteProviderProperties;
  }

  @Override
  public Response executeRemoteProviderRequestResponse(
      Request request,
      Class<Response> clazz,
      BiFunction<BackEndLogField, Logger, Supplier<Predicate<AbstractRemoteProviderResponse>>>
          errorResponseFunction) {

    // check if retry is enabled
    if (this.remoteProviderProperties.isRetryEnabled()) {
      int retryCount = 0;

      while (retryCount < this.remoteProviderProperties.getRetryCounter()) {

        try {
          return this.executePost(request, clazz, errorResponseFunction);
        } catch (InvalidTOTPException | InternalServerErrorException ie) {
          retryCount++;
          LOGGER.warn(
              "Encountered an exception while trying to execute " + request.getDescription(),
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
    return this.executePost(request, clazz, errorResponseFunction);
  }

  /**
   * executePost takes care of the internal business logic for connecting to the provider's's remote
   * http api in order to executeRemoteProviderRequestResponse the provided document
   */
  private Response executePost(
      Request request,
      Class<Response> clazz,
      BiFunction<BackEndLogField, Logger, Supplier<Predicate<AbstractRemoteProviderResponse>>>
          errorResponseFunction) {

    long start = System.currentTimeMillis();

    try {
      // generate new TOTP password
      request.setTOTP(
          request.getKey(), this.remoteProviderProperties.getTotpWaitForRefreshSeconds());

      // attempt to executeRemoteProviderRequestResponse with remote provider
      Response remoteProviderPostRequestResponse = this.doPost(request, clazz);

      String executionTime = Utils.formatTimePeriod(start);

      // check if the signing was successful
      if (!remoteProviderPostRequestResponse.getSuccess()) {

        BackEndLogField field =
            BackEndLogField.builder()
                .backendHost(this.remoteProviderProperties.getEndpoint())
                .details(remoteProviderPostRequestResponse.getErrorMessage())
                .executionTime(executionTime)
                .build();

        errorResponseFunction.apply(field, LOGGER).get().test((remoteProviderPostRequestResponse));
      }

      LOGGER.info(
          "Successful " + request.getDescription(),
          f(
              BackEndLogField.builder()
                  .backendHost(this.remoteProviderProperties.getEndpoint())
                  .executionTime(executionTime)
                  .build()));

      return remoteProviderPostRequestResponse;
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
    }
  }

  private Response doPost(Request request, Class<Response> clazz) throws IOException {

    StringEntity postBody = new StringEntity(request.toJSON());
    postBody.setContentType("application/json");

    // Set up a post request
    HttpPost postReq = new HttpPost(request.getUrl());
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
    return objectMapper.readValue(currentMsg.toString(), clazz);
  }
}
