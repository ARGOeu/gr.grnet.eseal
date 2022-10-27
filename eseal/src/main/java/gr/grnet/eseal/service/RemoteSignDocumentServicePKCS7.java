package gr.grnet.eseal.service;

import static net.logstash.logback.argument.StructuredArguments.f;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.BLevelParameters;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pdf.PDFSignatureService;
import eu.europa.esig.dss.pdf.pdfbox.PdfBoxNativeObjectFactory;
import eu.europa.esig.dss.utils.Utils;
import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.config.VisibleSignatureProperties;
import gr.grnet.eseal.dto.SignDocumentDto;
import gr.grnet.eseal.enums.Path;
import gr.grnet.eseal.enums.VisibleSignaturePosition;
import gr.grnet.eseal.exception.InternalServerErrorException;
import gr.grnet.eseal.logging.ServiceLogField;
import gr.grnet.eseal.sign.RemoteProviderCertificates;
import gr.grnet.eseal.sign.RemoteProviderSignBuffer;
import gr.grnet.eseal.sign.request.RemoteProviderSignBufferPKCS7Request;
import gr.grnet.eseal.sign.response.RemoteProviderSignBufferResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "remoteSignDocumentServiceDetachedPKCS7")
public class RemoteSignDocumentServicePKCS7 implements SignDocumentService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(RemoteSignDocumentServicePKCS7.class);

  private final PDFSignatureService pdfSignatureService;
  private final VisibleSignatureProperties visibleSignatureProperties;
  private final RemoteProviderProperties remoteProviderProperties;
  private final RemoteProviderSignBuffer remoteProviderSignBuffer;
  private final RemoteProviderCertificates remoteProviderCertificates;

  @Autowired
  public RemoteSignDocumentServicePKCS7(
      VisibleSignatureProperties visibleSignatureProperties,
      RemoteProviderProperties remoteProviderProperties,
      RemoteProviderSignBuffer remoteProviderSignBuffer,
      RemoteProviderCertificates remoteProviderCertificates) {
    this.visibleSignatureProperties = visibleSignatureProperties;
    this.remoteProviderProperties = remoteProviderProperties;
    this.remoteProviderSignBuffer = remoteProviderSignBuffer;
    this.remoteProviderCertificates = remoteProviderCertificates;
    PdfBoxNativeObjectFactory factory = new PdfBoxNativeObjectFactory();
    this.pdfSignatureService = factory.newPAdESSignatureService();
  }

  @Override
  public String signDocument(SignDocumentDto signDocumentDto) {
    DSSDocument toBeSignedDocument =
        new InMemoryDocument(Utils.fromBase64(signDocumentDto.getBytes()));
    DSSDocument signedDocument;

    SignatureImageParameters signatureImageParameters =
        getSignatureImageParameters(
            signDocumentDto.getSigningDate(),
            visibleSignatureProperties,
            signDocumentDto.getSignerInfo(),
            signDocumentDto.getImageBytes(),
            VisibleSignaturePosition.TOP_LEFT);

    // Initialize the PaDES parameters
    PAdESSignatureParameters padesSignatureParameters = new PAdESSignatureParameters();
    padesSignatureParameters.setImageParameters(signatureImageParameters);
    padesSignatureParameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
    padesSignatureParameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
    padesSignatureParameters.setContentSize(3 * 9472);
    BLevelParameters blevelParameters = new BLevelParameters();
    blevelParameters.setSigningDate(signDocumentDto.getSigningDate());
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
    RemoteProviderSignBufferPKCS7Request request = new RemoteProviderSignBufferPKCS7Request();
    request.setKey(signDocumentDto.getKey());
    request.setBufferToSign(Utils.toBase64(digestBytes));
    request.setUsername(signDocumentDto.getUsername());
    request.setPassword(signDocumentDto.getPassword());
    request.setUrl(
        String.format(
            "%s://%s/%s",
            "https", remoteProviderProperties.getEndpoint(), Path.REMOTE_SIGNING_BUFFER));

    RemoteProviderSignBufferResponse response =
        remoteProviderSignBuffer.executeRemoteProviderRequestResponse(
            request,
            RemoteProviderSignBufferResponse.class,
            SignDocumentService.errorResponseFunction());

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
}
