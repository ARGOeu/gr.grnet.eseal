package gr.grnet.eseal.service;

import static net.logstash.logback.argument.StructuredArguments.f;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.BLevelParameters;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pdf.PDFSignatureService;
import eu.europa.esig.dss.pdf.pdfbox.PdfBoxNativeObjectFactory;
import eu.europa.esig.dss.utils.Utils;
import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.exception.InternalServerErrorException;
import gr.grnet.eseal.exception.InvalidTOTPException;
import gr.grnet.eseal.exception.UnprocessableEntityException;
import gr.grnet.eseal.logging.BackEndLogField;
import gr.grnet.eseal.logging.ServiceLogField;
import gr.grnet.eseal.sign.RemoteHttpEsealClient;
import gr.grnet.eseal.sign.RemoteProviderSignBuffer;
import gr.grnet.eseal.sign.RemoteProviderSignDocument;
import gr.grnet.eseal.sign.request.RemoteProviderSignBufferRequest;
import gr.grnet.eseal.sign.request.RemoteProviderSignDocumentRequest;
import gr.grnet.eseal.sign.response.RemoteProviderSignBufferResponse;
import gr.grnet.eseal.sign.response.RemoteProviderSignDocumentResponse;
import gr.grnet.eseal.sign.response.RemoteProviderTOTPResponse;
import java.io.IOException;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignDocumentService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SignDocumentService.class);

  private RemoteProviderSignDocument remoteProviderSignDocument;
  private RemoteProviderSignBuffer remoteProviderSignBuffer;
  private RemoteProviderProperties remoteProviderProperties;
  private final String signingPath = "dsa/v1/sign";
  private final String signingBufferPath = "dsa/v1/SignBuffer";
  private final String protocol = "https";
  private PDFSignatureService pdfSignatureService;

  @Autowired
  public SignDocumentService(
      RemoteProviderSignDocument remoteProviderSignDocument,
      RemoteProviderSignBuffer remoteProviderSignBuffer,
      RemoteProviderProperties remoteProviderProperties) {
    this.remoteProviderSignDocument = remoteProviderSignDocument;
    this.remoteProviderSignBuffer = remoteProviderSignBuffer;
    this.remoteProviderProperties = remoteProviderProperties;
    PdfBoxNativeObjectFactory factory = new PdfBoxNativeObjectFactory();
    this.pdfSignatureService = factory.newPAdESSignatureService();
  }

  public String signDocumentDetached(
      String document, String username, String password, String key, Date signingDate) {

    DSSDocument toBeSignedDocument = new InMemoryDocument(Utils.fromBase64(document));
    DSSDocument signedDocument;

    // Initialize the PaDES parameters
    PAdESSignatureParameters padesSignatureParameters = new PAdESSignatureParameters();
    padesSignatureParameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
    padesSignatureParameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
    padesSignatureParameters.setContentSize(3 * 9472);
    if (signingDate != null) {
      BLevelParameters blevelParameters = new BLevelParameters();
      blevelParameters.setSigningDate(signingDate);
      padesSignatureParameters.setBLevelParams(blevelParameters);
    }

    byte[] digestBytes;
    // compute the digest of the PDF document
    try {
      digestBytes = this.pdfSignatureService.digest(toBeSignedDocument, padesSignatureParameters);
    } catch (DSSException de) {
      LOGGER.error(
          "DSS Error while computing digest",
          f(ServiceLogField.builder().details(de.getMessage()).build()));
      throw new InternalServerErrorException("Could not compute document digest");
    }
    RemoteProviderSignBufferRequest request = new RemoteProviderSignBufferRequest();
    request.setKey(key);
    request.setBufferToSign(Utils.toBase64(digestBytes));
    request.setUsername(username);
    request.setPassword(password);
    request.setUrl(
        String.format(
            "%s://%s/%s", protocol, remoteProviderProperties.getEndpoint(), signingBufferPath));

    RemoteProviderSignBufferResponse response =
        remoteProviderSignBuffer.sign(
            request, RemoteProviderSignBufferResponse.class, errorResponseFunction());

    // combine signature with original document
    try {
      signedDocument =
          this.pdfSignatureService.sign(
              toBeSignedDocument,
              Utils.fromBase64(response.getSignature()),
              padesSignatureParameters);
    } catch (DSSException de) {
      LOGGER.error(
          "DSS Error while combining signature to original document",
          f(ServiceLogField.builder().details(de.getMessage()).build()));
      throw new InternalServerErrorException("Could not combine signature to original document");
    }

    String signedDocumentB64;

    try {
      signedDocumentB64 = Utils.toBase64(Utils.toByteArray(signedDocument.openStream()));
    } catch (IOException e) {
      LOGGER.error(
          "Error converting signed pdf to base64",
          f(ServiceLogField.builder().details(e.getMessage()).build()));
      throw new InternalServerErrorException("Could not produce signed document");
    }

    return signedDocumentB64;
  }

  public String signDocument(String document, String username, String password, String key) {

    RemoteProviderSignDocumentRequest request = new RemoteProviderSignDocumentRequest();
    request.setKey(key);
    request.setFileData(document);
    request.setUsername(username);
    request.setPassword(password);
    request.setUrl(
        String.format("%s://%s/%s", protocol, remoteProviderProperties.getEndpoint(), signingPath));

    RemoteProviderSignDocumentResponse response =
        remoteProviderSignDocument.sign(
            request, RemoteProviderSignDocumentResponse.class, errorResponseFunction());

    return response.getSignedFileData();
  }

  public void setRemoteProviderSignDocument(RemoteProviderSignDocument remoteProviderSignDocument) {
    this.remoteProviderSignDocument = remoteProviderSignDocument;
  }

  private static BiFunction<
          BackEndLogField, Logger, Supplier<Predicate<RemoteProviderTOTPResponse>>>
      errorResponseFunction() {
    return (field, logger) ->
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
  }
}
