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
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.ListCertificateSource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.CRLFirstRevocationDataLoadingStrategyFactory;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import gr.grnet.eseal.config.DocumentValidatorLOTLBean;
import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.config.VisibleSignatureProperties;
import gr.grnet.eseal.dto.SignDocumentDto;
import gr.grnet.eseal.enums.Path;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service(value = "remoteSignDocumentServicePKCS1")
public class RemoteSignDocumentServicePKCS1 implements SignDocumentService {

  @Autowired private TrustedCertSourcesBean trustedCertSourcesBean;

  @Autowired private DocumentValidatorLOTLBean lotlBean;

  @Value("${eseal.manual.truststore.enabled:false}")
  private boolean trustedCertSourcesBeanEnabled;

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

        CommonCertificateVerifier certVerifier = new CommonCertificateVerifier();

        // has to be true because otherwise DSS will not attempt to check revocations for
        // untrusted certificates at all, even when this is required
        // eg. while extending signatures and will always fail in the next step
        certVerifier.setCheckRevocationForUntrustedChains(true);

        // the below step was tried out in a scenario where some TSA certifiates didn't have
        // AIA information included and DSS tried to build full certificate chain for them
        // but since for the particular scenario the certificate was also included in LOTL
        // we get around that problem by including LOTL list as trusted source too
        if (trustedCertSourcesBeanEnabled) {
          certVerifier.addTrustedCertSources(trustedCertSourcesBean.getSource());
        }

        // include LOTL as trusted source so that DSS doesn't fail when full cert chain
        // cannot be built due to missing AIA / OCSP info
        ListCertificateSource lotl =
            lotlBean.getLotlValidator().getCertificateVerifier().getTrustedCertSources();
        CertificateSource[] lotlAsArray =
            lotl.getSources().stream().toArray(CertificateSource[]::new);
        certVerifier.addTrustedCertSources(lotlAsArray);

        // CRLSource
        OnlineCRLSource onlineCRLSource = new OnlineCRLSource();
        onlineCRLSource.setDataLoader(this.commonsDataLoaderWithCustomTimeouts());
        certVerifier.setCrlSource(onlineCRLSource);

        // OCSPSource
        OnlineOCSPSource onlineOCSPSource = new OnlineOCSPSource();
        OCSPDataLoader ocspDataLoader = new OCSPDataLoader();
        ocspDataLoader.setTimeoutConnection(this.remoteProviderProperties.getConnectTimeout());
        ocspDataLoader.setTimeoutSocket(this.remoteProviderProperties.getSocketConnectTimeout());
        ocspDataLoader.setTimeoutConnectionRequest(
            this.remoteProviderProperties.getRequestConnectTimeout());
        onlineOCSPSource.setDataLoader(ocspDataLoader);
        certVerifier.setOcspSource(onlineOCSPSource);

        // AIA Source
        certVerifier.setAIASource(new DefaultAIASource(this.commonsDataLoaderWithCustomTimeouts()));

        certVerifier.setAlertOnMissingRevocationData(new ExceptionOnStatusAlert());
        certVerifier.setAlertOnUncoveredPOE(new LogOnStatusAlert());
        certVerifier.setAlertOnRevokedCertificate(new ExceptionOnStatusAlert());
        certVerifier.setAlertOnInvalidTimestamp(new ExceptionOnStatusAlert());
        certVerifier.setAlertOnNoRevocationAfterBestSignatureTime(new LogOnStatusAlert());
        certVerifier.setAlertOnExpiredSignature(new ExceptionOnStatusAlert());

        // since DSS 5.11 it is required to set the factory, instead of the strategy
        // in order to circumvent concurrency issues
        // https://dss.nowina.lu/doc/dss-documentation.html#certificateVerifier
        certVerifier.setRevocationDataLoadingStrategyFactory(
            new CRLFirstRevocationDataLoadingStrategyFactory());

        PAdESService padesService = new PAdESService(certVerifier);
        padesService.setTspSource(tsaSourceRegistry.getCompositeTSASource());

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
    cdl.setTimeoutConnection(this.remoteProviderProperties.getConnectTimeout());
    cdl.setTimeoutSocket(this.remoteProviderProperties.getSocketConnectTimeout());
    cdl.setTimeoutConnectionRequest(this.remoteProviderProperties.getRequestConnectTimeout());
    return cdl;
  }
}
