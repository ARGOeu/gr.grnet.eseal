package gr.grnet.eseal.service;

import static net.logstash.logback.argument.StructuredArguments.f;

import eu.europa.esig.dss.alert.ExceptionOnStatusAlert;
import eu.europa.esig.dss.alert.LogOnStatusAlert;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.BLevelParameters;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.config.VisibleSignatureProperties;
import gr.grnet.eseal.dto.SignDocumentDto;
import gr.grnet.eseal.enums.Path;
import gr.grnet.eseal.enums.TSASourceEnum;
import gr.grnet.eseal.enums.VisibleSignaturePosition;
import gr.grnet.eseal.exception.InternalServerErrorException;
import gr.grnet.eseal.logging.ServiceLogField;
import gr.grnet.eseal.sign.RemoteProviderSignBuffer;
import gr.grnet.eseal.sign.request.RemoteProviderSignBufferPKCS1Request;
import gr.grnet.eseal.sign.response.RemoteProviderSignBufferResponse;
import gr.grnet.eseal.timestamp.TSASourceRegistry;
import java.security.MessageDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "remoteSignDocumentServicePKCS1")
public class RemoteSignDocumentServicePKCS1 implements SignDocumentService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(RemoteSignDocumentServicePKCS1.class);

  private final VisibleSignatureProperties visibleSignatureProperties;
  private final TSASourceRegistry tsaSourceRegistry;
  private final RemoteProviderProperties remoteProviderProperties;
  private final RemoteProviderSignBuffer remoteProviderSignBuffer;

  @Autowired
  public RemoteSignDocumentServicePKCS1(
      VisibleSignatureProperties visibleSignatureProperties,
      TSASourceRegistry tsaSourceRegistry,
      RemoteProviderProperties remoteProviderProperties,
      RemoteProviderSignBuffer remoteProviderSignBuffer) {
    this.visibleSignatureProperties = visibleSignatureProperties;
    this.tsaSourceRegistry = tsaSourceRegistry;
    this.remoteProviderProperties = remoteProviderProperties;
    this.remoteProviderSignBuffer = remoteProviderSignBuffer;
  }

  @Override
  public String signDocument(SignDocumentDto signDocumentDto) {

    boolean retryVisibleSignature = true;
    int visibleSignaturePosition = 0;
    String base64SignedDocument = "";

    while (retryVisibleSignature) {
      try {
        PAdESSignatureParameters padesSignatureParameters = new PAdESSignatureParameters();
        padesSignatureParameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_LTA);
        padesSignatureParameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
        padesSignatureParameters.setContentSize(3 * 9472);
        BLevelParameters blevelParameters = new BLevelParameters();
        blevelParameters.setSigningDate(signDocumentDto.getSigningDate());
        padesSignatureParameters.setBLevelParams(blevelParameters);
        padesSignatureParameters.setSigningCertificate(signDocumentDto.getCertificateList().get(0));
        padesSignatureParameters.setCertificateChain(signDocumentDto.getCertificateList());
        if (signDocumentDto.getImageVisibility()) {
          SignatureImageParameters signatureImageParameters =
              getSignatureImageParameters(
                  signDocumentDto.getSigningDate(),
                  visibleSignatureProperties,
                  signDocumentDto.getSignerInfo(),
                  signDocumentDto.getImageBytes(),
                  VisibleSignaturePosition.getPosition(visibleSignaturePosition));
          padesSignatureParameters.setImageParameters(signatureImageParameters);
        }

        CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
        commonCertificateVerifier.setCheckRevocationForUntrustedChains(true);
        commonCertificateVerifier.setAlertOnMissingRevocationData(new ExceptionOnStatusAlert());

        // CRLSource
        OnlineCRLSource onlineCRLSource = new OnlineCRLSource();
        onlineCRLSource.setDataLoader(this.commonsDataLoaderWithCustomTimeouts());
        commonCertificateVerifier.setCrlSource(onlineCRLSource);

        // OCSPSource
        OnlineOCSPSource onlineOCSPSource = new OnlineOCSPSource();
        OCSPDataLoader ocspDataLoader = new OCSPDataLoader();
        ocspDataLoader.setTimeoutConnection(this.remoteProviderProperties.getConnectTimeout());
        ocspDataLoader.setTimeoutSocket(this.remoteProviderProperties.getSocketConnectTimeout());
        ocspDataLoader.setTimeoutConnectionRequest(
            this.remoteProviderProperties.getRequestConnectTimeout());
        onlineOCSPSource.setDataLoader(ocspDataLoader);
        commonCertificateVerifier.setOcspSource(onlineOCSPSource);

        // AIA Source
        commonCertificateVerifier.setAIASource(
            new DefaultAIASource(this.commonsDataLoaderWithCustomTimeouts()));

        commonCertificateVerifier.setAlertOnMissingRevocationData(new ExceptionOnStatusAlert());
        commonCertificateVerifier.setAlertOnUncoveredPOE(new LogOnStatusAlert());
        commonCertificateVerifier.setAlertOnRevokedCertificate(new ExceptionOnStatusAlert());
        commonCertificateVerifier.setAlertOnInvalidTimestamp(new ExceptionOnStatusAlert());
        commonCertificateVerifier.setAlertOnNoRevocationAfterBestSignatureTime(
            new LogOnStatusAlert());
        commonCertificateVerifier.setAlertOnExpiredSignature(new ExceptionOnStatusAlert());

        PAdESService padesService = new PAdESService(commonCertificateVerifier);
        padesService.setTspSource(tsaSourceRegistry.getTSASource(TSASourceEnum.HARICA));

        DSSDocument toBeSignedDocument =
            new InMemoryDocument(Utils.fromBase64(signDocumentDto.getBytes()));

        ToBeSigned dataToSign =
            padesService.getDataToSign(toBeSignedDocument, padesSignatureParameters);

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        messageDigest.update(dataToSign.getBytes());

        byte[] digestBytes = messageDigest.digest();

        RemoteProviderSignBufferPKCS1Request request = new RemoteProviderSignBufferPKCS1Request();
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

        SignatureValue signatureValue =
            new SignatureValue(
                SignatureAlgorithm.RSA_SHA256, Utils.fromBase64(response.getSignature()));

        DSSDocument signedDocument =
            padesService.signDocument(toBeSignedDocument, padesSignatureParameters, signatureValue);

        retryVisibleSignature = false;
        base64SignedDocument = Utils.toBase64(Utils.toByteArray(signedDocument.openStream()));

      } catch (Exception e) {
        LOGGER.error(
            "Could not produce signed document",
            f(ServiceLogField.builder().details(e.getMessage()).build()));
        if (e.getMessage()
            .contains("The new signature field position overlaps with an existing annotation!")) {
          visibleSignaturePosition++;
          if (VisibleSignaturePosition.isLastPossiblePosition(visibleSignaturePosition)) {
            signDocumentDto.setImageVisibility(false);
          }
          LOGGER.info(
              String.format(
                  "Retrying new visible image position(%s)",
                  VisibleSignaturePosition.getPosition(visibleSignaturePosition)),
              f(ServiceLogField.builder().details(e.getMessage()).build()));
        } else {
          e.printStackTrace();
          throw new InternalServerErrorException(
              "Could not produce signed document." + e.getMessage());
        }
      }
    }
    return base64SignedDocument;
  }

  private CommonsDataLoader commonsDataLoaderWithCustomTimeouts() {
    CommonsDataLoader cdl = new CommonsDataLoader();
    cdl.setTimeoutConnection(this.remoteProviderProperties.getConnectTimeout() * 1000);
    cdl.setTimeoutSocket(this.remoteProviderProperties.getSocketConnectTimeout() * 10000);
    cdl.setTimeoutConnectionRequest(
        this.remoteProviderProperties.getRequestConnectTimeout() * 1000);
    return cdl;
  }
}
