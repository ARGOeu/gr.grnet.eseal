package gr.grnet.eseal.service;

import static net.logstash.logback.argument.StructuredArguments.f;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignerTextHorizontalAlignment;
import eu.europa.esig.dss.enumerations.VisualSignatureAlignmentHorizontal;
import eu.europa.esig.dss.enumerations.VisualSignatureAlignmentVertical;
import eu.europa.esig.dss.model.BLevelParameters;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.pdf.PDFSignatureService;
import eu.europa.esig.dss.pdf.pdfbox.PdfBoxNativeObjectFactory;
import eu.europa.esig.dss.utils.Utils;
import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.config.VisibleSignatureProperties;
import gr.grnet.eseal.exception.InternalServerErrorException;
import gr.grnet.eseal.exception.InvalidTOTPException;
import gr.grnet.eseal.exception.UnprocessableEntityException;
import gr.grnet.eseal.logging.BackEndLogField;
import gr.grnet.eseal.logging.ServiceLogField;
import gr.grnet.eseal.sign.RemoteHttpEsealClient;
import gr.grnet.eseal.sign.RemoteProviderCertificates;
import gr.grnet.eseal.sign.RemoteProviderSignBuffer;
import gr.grnet.eseal.sign.RemoteProviderSignDocument;
import gr.grnet.eseal.sign.request.RemoteProviderCertificatesRequest;
import gr.grnet.eseal.sign.request.RemoteProviderSignBufferRequest;
import gr.grnet.eseal.sign.request.RemoteProviderSignDocumentRequest;
import gr.grnet.eseal.sign.response.AbstractRemoteProviderResponse;
import gr.grnet.eseal.sign.response.RemoteProviderCertificatesResponse;
import gr.grnet.eseal.sign.response.RemoteProviderSignBufferResponse;
import gr.grnet.eseal.sign.response.RemoteProviderSignDocumentResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
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
  private RemoteProviderCertificates remoteProviderCertificates;
  private RemoteProviderProperties remoteProviderProperties;
  private VisibleSignatureProperties visibleSignatureProperties;
  private final String signingPath = "dsa/v1/sign";
  private final String signingBufferPath = "dsa/v1/SignBuffer";
  private final String certificatesPath = "dsa/v1/Certificates";
  private final String protocol = "https";
  private PDFSignatureService pdfSignatureService;

  @Autowired
  public SignDocumentService(
      RemoteProviderSignDocument remoteProviderSignDocument,
      RemoteProviderSignBuffer remoteProviderSignBuffer,
      RemoteProviderCertificates remoteProviderCertificates,
      RemoteProviderProperties remoteProviderProperties,
      VisibleSignatureProperties visibleSignatureProperties) {
    this.remoteProviderSignDocument = remoteProviderSignDocument;
    this.remoteProviderSignBuffer = remoteProviderSignBuffer;
    this.remoteProviderCertificates = remoteProviderCertificates;
    this.remoteProviderProperties = remoteProviderProperties;
    this.visibleSignatureProperties = visibleSignatureProperties;
    PdfBoxNativeObjectFactory factory = new PdfBoxNativeObjectFactory();
    this.pdfSignatureService = factory.newPAdESSignatureService();
  }

  public String getSignerInfo(String username, String password) {

    RemoteProviderCertificatesRequest remoteProviderCertificatesRequest =
        new RemoteProviderCertificatesRequest();
    remoteProviderCertificatesRequest.setUsername(username);
    remoteProviderCertificatesRequest.setPassword(password);
    remoteProviderCertificatesRequest.setUrl(
        String.format(
            "%s://%s/%s", protocol, remoteProviderProperties.getEndpoint(), certificatesPath));

    RemoteProviderCertificatesResponse remoteProviderCertificatesResponse =
        this.remoteProviderCertificates.executeRemoteProviderRequestResponse(
            remoteProviderCertificatesRequest,
            RemoteProviderCertificatesResponse.class,
            errorResponseFunction());

    try {
      return gr.grnet.eseal.utils.Utils.extractCNFromSubject(
              remoteProviderCertificatesResponse.getSubject())
          + "/"
          + gr.grnet.eseal.utils.Utils.extractOUFromSubject(
              remoteProviderCertificatesResponse.getSubject());
    } catch (Exception e) {
      LOGGER.error(
          "Error with Signer's Certificate Subject ",
          f(ServiceLogField.builder().details(e.getMessage()).build()));
      throw new InternalServerErrorException("Error with Signer's Certificate Subject");
    }
  }

  public String signDocumentDetached(
      String document,
      String username,
      String password,
      String key,
      Date signingDate,
      String signerInfo,
      DSSDocument imageDocument) {

    DSSDocument toBeSignedDocument = new InMemoryDocument(Utils.fromBase64(document));
    DSSDocument signedDocument;

    // visible signature text
    ZonedDateTime z =
        ZonedDateTime.ofInstant(
            signingDate.toInstant(), this.visibleSignatureProperties.getZoneId());

    SignatureImageTextParameters signatureImageTextParameters = new SignatureImageTextParameters();
    signatureImageTextParameters.setText(
        gr.grnet.eseal.utils.Utils.formatVisibleSignatureText(
            signerInfo, z.format(this.visibleSignatureProperties.getDateTimeFormatter())));

    signatureImageTextParameters.setFont(this.visibleSignatureProperties.getFont());
    signatureImageTextParameters.setSignerTextHorizontalAlignment(
        SignerTextHorizontalAlignment.LEFT);

    // visible signature image
    SignatureImageParameters signatureImageParameters = new SignatureImageParameters();
    signatureImageParameters.setTextParameters(signatureImageTextParameters);

    // check if an image has been provided, otherwise use the default
    if (imageDocument != null) {
      signatureImageParameters.setImage(imageDocument);
    } else {
      signatureImageParameters.setImage(this.visibleSignatureProperties.getImageDocument());
    }
    signatureImageParameters.setAlignmentHorizontal(VisualSignatureAlignmentHorizontal.LEFT);
    signatureImageParameters.setAlignmentVertical(VisualSignatureAlignmentVertical.TOP);

    // Initialize the PaDES parameters
    PAdESSignatureParameters padesSignatureParameters = new PAdESSignatureParameters();
    padesSignatureParameters.setImageParameters(signatureImageParameters);
    padesSignatureParameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
    padesSignatureParameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
    padesSignatureParameters.setContentSize(3 * 9472);
    BLevelParameters blevelParameters = new BLevelParameters();
    blevelParameters.setSigningDate(signingDate);
    padesSignatureParameters.setBLevelParams(blevelParameters);

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
        remoteProviderSignBuffer.executeRemoteProviderRequestResponse(
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
        remoteProviderSignDocument.executeRemoteProviderRequestResponse(
            request, RemoteProviderSignDocumentResponse.class, errorResponseFunction());

    return response.getSignedFileData();
  }

  public void setRemoteProviderSignDocument(RemoteProviderSignDocument remoteProviderSignDocument) {
    this.remoteProviderSignDocument = remoteProviderSignDocument;
  }

  private static BiFunction<
          BackEndLogField, Logger, Supplier<Predicate<AbstractRemoteProviderResponse>>>
      errorResponseFunction() {
    return (field, logger) ->
        () ->
            RemoteHttpEsealClient.errorResponsePredicate(
                    "The user is locked",
                    field,
                    (r) -> r.getErrorMessage().contains("The user is locked"),
                    new InternalServerErrorException("The user is locked and cannot logon"),
                    logger)
                .or(
                    RemoteHttpEsealClient.errorResponsePredicate(
                        "Connection to Time Stamping service problem",
                        field,
                        (r) ->
                            r.getErrorMessage()
                                .contains("Connection to Time Stamping service problem"),
                        new InternalServerErrorException(
                            "Connection to Time Stamping service problem"),
                        logger))
                .or(
                    RemoteHttpEsealClient.errorResponsePredicate(
                        "Failed to login",
                        field,
                        (r) -> r.getErrorMessage().contains("Failed to Logon"),
                        new UnprocessableEntityException("Wrong user credentials"),
                        logger))
                .or(
                    RemoteHttpEsealClient.errorResponsePredicate(
                        "Failed to login",
                        field,
                        (r) ->
                            r.getErrorMessage()
                                .contains(
                                    "(0X90020133)-Failed to allocated resources"
                                        + " for a new SAPI-LOGIN session (dynamic slot)."),
                        new UnprocessableEntityException("Wrong user credentials"),
                        logger))
                .or(
                    RemoteHttpEsealClient.errorResponsePredicate(
                        "Failed to get the URL of the OCSP server",
                        field,
                        (r) ->
                            r.getErrorMessage()
                                .contains(
                                    "Failed to Sign, Error (0X90030233)-"
                                        + "Failed to get the URL of the OCSP server."),
                        new InternalServerErrorException(
                            "Failed to get the URL of the OCSP server"),
                        logger))
                .or(
                    RemoteHttpEsealClient.errorResponsePredicate(
                        "Invalid TOTP",
                        field,
                        (r) ->
                            r.getErrorMessage()
                                .contains(
                                    "Failed to Sign, Error (0X900201E0)-"
                                        + "Failed to verify the user password. "
                                        + "Passwords should be in wide character representation. "
                                        + "Password length in bytes includes "
                                        + "the null terminator "
                                        + "(two bytes in wide char representation)."),
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
