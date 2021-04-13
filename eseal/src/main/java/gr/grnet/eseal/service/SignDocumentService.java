package gr.grnet.eseal.service;

import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.exception.InternalServerErrorException;
import gr.grnet.eseal.exception.InvalidTOTPException;
import gr.grnet.eseal.exception.UnprocessableEntityException;
import gr.grnet.eseal.logging.BackEndLogField;
import gr.grnet.eseal.sign.RemoteHttpEsealClient;
import gr.grnet.eseal.sign.RemoteProviderSignDocument;
import gr.grnet.eseal.sign.request.RemoteProviderSignDocumentRequest;
import gr.grnet.eseal.sign.response.RemoteProviderSignDocumentResponse;
import gr.grnet.eseal.sign.response.RemoteProviderTOTPResponse;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignDocumentService {

  private RemoteProviderSignDocument remoteProviderSignDocument;
  private RemoteProviderProperties remoteProviderProperties;
  private final String signingPath = "dsa/v1/sign";
  private final String protocol = "https";

  @Autowired
  public SignDocumentService(
      RemoteProviderSignDocument remoteProviderSignDocument,
      RemoteProviderProperties remoteProviderProperties) {
    this.remoteProviderSignDocument = remoteProviderSignDocument;
    this.remoteProviderProperties = remoteProviderProperties;
  }

  public String signDocument(String document, String username, String password, String key) {

    RemoteProviderSignDocumentRequest request = new RemoteProviderSignDocumentRequest();
    request.setKey(key);
    request.setFileData(document);
    request.setUsername(username);
    request.setPassword(password);
    request.setUrl(
        String.format("%s://%s/%s", protocol, remoteProviderProperties.getEndpoint(), signingPath));

    BiFunction<BackEndLogField, Logger, Supplier<Predicate<RemoteProviderTOTPResponse>>>
        errorResponseFunction =
            (field, logger) ->
                () ->
                    RemoteHttpEsealClient.errorResponsePredicate(
                            "The user is locked",
                            field,
                            (r) -> r.getErrorData().getMessage().contains("The user is locked"),
                            new InternalServerErrorException("The user is locked and cannot logon"),
                            logger)
                        .or(
                            RemoteHttpEsealClient.errorResponsePredicate(
                                "Connection to Time Stamping service problem",
                                field,
                                (r) ->
                                    r.getErrorData()
                                        .getMessage()
                                        .contains("Connection to Time Stamping service problem"),
                                new InternalServerErrorException(
                                    "Connection to Time Stamping service problem"),
                                logger))
                        .or(
                            RemoteHttpEsealClient.errorResponsePredicate(
                                "Failed to login",
                                field,
                                (r) -> r.getErrorData().getMessage().contains("Failed to Logon"),
                                new UnprocessableEntityException("Wrong user credentials"),
                                logger))
                        .or(
                            RemoteHttpEsealClient.errorResponsePredicate(
                                "Invalid TOTP",
                                field,
                                (r) -> r.getErrorData().getMessage().contains("Failed to Sign"),
                                new InvalidTOTPException(),
                                logger))
                        .or(
                            RemoteHttpEsealClient.errorResponsePredicate(
                                "Error response from provider",
                                field,
                                (r) -> true,
                                new InternalServerErrorException("Error with signing backend"),
                                logger));

    RemoteProviderSignDocumentResponse response =
        remoteProviderSignDocument.sign(
            request, RemoteProviderSignDocumentResponse.class, errorResponseFunction);

    return response.getSignedFileData();
  }

  public void setRemoteProviderSignDocument(RemoteProviderSignDocument remoteProviderSignDocument) {
    this.remoteProviderSignDocument = remoteProviderSignDocument;
  }
}
