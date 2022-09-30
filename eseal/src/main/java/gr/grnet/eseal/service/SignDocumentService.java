package gr.grnet.eseal.service;

import eu.europa.esig.dss.enumerations.SignerTextHorizontalAlignment;
import eu.europa.esig.dss.enumerations.VisualSignatureAlignmentHorizontal;
import eu.europa.esig.dss.enumerations.VisualSignatureAlignmentVertical;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.utils.Utils;
import gr.grnet.eseal.config.VisibleSignatureProperties;
import gr.grnet.eseal.dto.SignDocumentDto;
import gr.grnet.eseal.enums.Path;
import gr.grnet.eseal.enums.VisibleSignaturePosition;
import gr.grnet.eseal.enums.VisibleSignatureText;
import gr.grnet.eseal.exception.InternalServerErrorException;
import gr.grnet.eseal.exception.InvalidTOTPException;
import gr.grnet.eseal.exception.UnprocessableEntityException;
import gr.grnet.eseal.logging.BackEndLogField;
import gr.grnet.eseal.sign.RemoteHttpEsealClient;
import gr.grnet.eseal.sign.RemoteProviderCertificates;
import gr.grnet.eseal.sign.request.RemoteProviderCertificatesRequest;
import gr.grnet.eseal.sign.response.AbstractRemoteProviderResponse;
import gr.grnet.eseal.sign.response.RemoteProviderCertificatesResponse;
import io.vavr.CheckedFunction1;
import io.vavr.control.Try;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public interface SignDocumentService {

  String signDocument(SignDocumentDto signDocumentDto);

  static BiFunction<BackEndLogField, Logger, Supplier<Predicate<AbstractRemoteProviderResponse>>>
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

  static RemoteProviderCertificatesResponse getUserCertificates(
      String username,
      String password,
      String endpoint,
      RemoteProviderCertificates remoteProviderCertificates) {

    RemoteProviderCertificatesRequest remoteProviderCertificatesRequest =
        new RemoteProviderCertificatesRequest();
    remoteProviderCertificatesRequest.setUsername(username);
    remoteProviderCertificatesRequest.setPassword(password);
    remoteProviderCertificatesRequest.setUrl(
        String.format("%s://%s/%s", "https", endpoint, Path.REMOTE_CERTIFICATES));

    return remoteProviderCertificates.executeRemoteProviderRequestResponse(
        remoteProviderCertificatesRequest,
        RemoteProviderCertificatesResponse.class,
        errorResponseFunction());
  }

  static String getSignerInfo(
      RemoteProviderCertificatesResponse certificates, VisibleSignatureText visibleSignatureText) {

    switch (visibleSignatureText) {
      case CN_OU:
        return Try.of(
                () ->
                    gr.grnet.eseal.utils.Utils.extractCNFromSubject(certificates.getSubject())
                        + "/"
                        + gr.grnet.eseal.utils.Utils.extractOUFromSubject(
                            certificates.getSubject()))
            .getOrElseThrow(
                (e) -> {
                  throw new InternalServerErrorException("Error with Signer's Certificate Subject");
                });
      case CN:
        return Try.of(
                () -> gr.grnet.eseal.utils.Utils.extractCNFromSubject(certificates.getSubject()))
            .getOrElseThrow(
                (e) -> {
                  throw new InternalServerErrorException("Error with Signer's Certificate Subject");
                });
      case OU:
        return Try.of(
                () -> gr.grnet.eseal.utils.Utils.extractOUFromSubject(certificates.getSubject()))
            .getOrElseThrow(
                (e) -> {
                  throw new InternalServerErrorException(
                      "Error with Signer's Certificate Organisational Unit");
                });
      case STATIC:
        return "Ο.Σ.Δ.Δ.Υ.Δ.Δ.";
      case TEXT:
        throw new InternalServerErrorException(
            "Using plain text on visible signature currently is not supported");
      default:
        return Try.of(
                () -> gr.grnet.eseal.utils.Utils.extractOUFromSubject(certificates.getSubject()))
            .getOrElseThrow(
                (e) -> {
                  throw new InternalServerErrorException("Error with Signer's Certificate Subject");
                });
    }
  }

  static List<CertificateToken> getCertificatesToken(
      RemoteProviderCertificatesResponse certificates) {

    return Arrays.stream(certificates.getCertificates())
        .map(Utils::fromBase64)
        .map(ByteArrayInputStream::new)
        .map(
            CheckedFunction1.liftTry(
                inputStream -> {
                  CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                  return (X509Certificate) certFactory.generateCertificate(inputStream);
                }))
        .map(
            tryCert ->
                tryCert.getOrElseThrow(
                    (e) -> {
                      throw new InternalServerErrorException("Error during Certificate parsing");
                    }))
        .map(CertificateToken::new)
        .collect(Collectors.toList());
  }

  default SignatureImageParameters getSignatureImageParameters(
      Date signingDate,
      VisibleSignatureProperties visibleSignatureProperties,
      String signerInfo,
      String imageBytes,
      VisibleSignaturePosition position) {

    // visible signature text
    ZonedDateTime z =
        ZonedDateTime.ofInstant(signingDate.toInstant(), visibleSignatureProperties.getZoneId());

    SignatureImageTextParameters signatureImageTextParameters = new SignatureImageTextParameters();
    signatureImageTextParameters.setText(
        gr.grnet.eseal.utils.Utils.formatVisibleSignatureText(
            signerInfo, z.format(visibleSignatureProperties.getDateTimeFormatter())));

    signatureImageTextParameters.setFont(visibleSignatureProperties.getFont());
    signatureImageTextParameters.setSignerTextHorizontalAlignment(
        SignerTextHorizontalAlignment.LEFT);

    // visible signature image
    SignatureImageParameters signatureImageParameters = new SignatureImageParameters();
    signatureImageParameters.setTextParameters(signatureImageTextParameters);

    // check if an image has been provided, otherwise use the default
    if (StringUtils.isNotEmpty(imageBytes)) {
      signatureImageParameters.setImage(new InMemoryDocument(Utils.fromBase64(imageBytes)));
    } else {
      signatureImageParameters.setImage(visibleSignatureProperties.getImageDocument());
    }

    switch (position) {
      case TOP_LEFT:
        signatureImageParameters.setAlignmentHorizontal(VisualSignatureAlignmentHorizontal.LEFT);
        signatureImageParameters.setAlignmentVertical(VisualSignatureAlignmentVertical.TOP);
        break;
      case TOP_RIGHT:
        signatureImageParameters.setAlignmentHorizontal(VisualSignatureAlignmentHorizontal.RIGHT);
        signatureImageParameters.setAlignmentVertical(VisualSignatureAlignmentVertical.TOP);
        break;
      case BOTTOM_LEFT:
        signatureImageParameters.setAlignmentHorizontal(VisualSignatureAlignmentHorizontal.LEFT);
        signatureImageParameters.setAlignmentVertical(VisualSignatureAlignmentVertical.BOTTOM);
        break;
      case BOTTOM_RIGHT:
        signatureImageParameters.setAlignmentHorizontal(VisualSignatureAlignmentHorizontal.RIGHT);
        signatureImageParameters.setAlignmentVertical(VisualSignatureAlignmentVertical.BOTTOM);
        break;
      default:
        break;
    }

    return signatureImageParameters;
  }
}
