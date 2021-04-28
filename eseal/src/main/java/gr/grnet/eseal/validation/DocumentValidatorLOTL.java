package gr.grnet.eseal.validation;

import static net.logstash.logback.argument.StructuredArguments.f;

import eu.europa.esig.dss.alert.ExceptionOnStatusAlert;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.tsl.alerts.LOTLAlert;
import eu.europa.esig.dss.tsl.alerts.TLAlert;
import eu.europa.esig.dss.tsl.alerts.detections.LOTLLocationChangeDetection;
import eu.europa.esig.dss.tsl.alerts.detections.OJUrlChangeDetection;
import eu.europa.esig.dss.tsl.alerts.detections.TLExpirationDetection;
import eu.europa.esig.dss.tsl.alerts.detections.TLSignatureErrorDetection;
import eu.europa.esig.dss.tsl.alerts.handlers.log.LogLOTLLocationChangeAlertHandler;
import eu.europa.esig.dss.tsl.alerts.handlers.log.LogOJUrlChangeAlertHandler;
import eu.europa.esig.dss.tsl.alerts.handlers.log.LogTLExpirationAlertHandler;
import eu.europa.esig.dss.tsl.alerts.handlers.log.LogTLSignatureErrorAlertHandler;
import eu.europa.esig.dss.tsl.function.OfficialJournalSchemeInformationURI;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import gr.grnet.eseal.config.ValidationProperties;
import gr.grnet.eseal.logging.ServiceLogField;
import gr.grnet.eseal.utils.Utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DocumentValidatorLOTL is used in the document validation process using the european trusted list
 * as trust source material
 */
public class DocumentValidatorLOTL {

  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentValidatorLOTL.class);

  private final String lotlCacheFolder = "lotl-cache";

  private ValidationProperties validationProperties;

  private CertificateVerifier certificateVerifier;

  private TLValidationJob job;

  public DocumentValidatorLOTL(ValidationProperties validationProperties) {
    this.validationProperties = validationProperties;
  }

  public CertificateVerifier getCertificateVerifier() {
    return certificateVerifier;
  }

  /**
   * initialize sets up the internal lotl validation job(pulling and loading all the needed trusted
   * material) and the certificate verifier that utilizes the trust sources to perform the document
   * validation
   */
  public void initialize() throws Exception {

    // initialize the the tl validation job
    this.job = initializeTLValidationJob();
    /// initialize the certificate verifier
    this.certificateVerifier = initializeCertificateVerifier();

    // add to both the tl validation job and the certificate verifier
    // the common trusted list source
    TrustedListsCertificateSource trustedListsCertificateSource =
        new TrustedListsCertificateSource();
    this.job.setTrustedListCertificateSource(trustedListsCertificateSource);
    this.certificateVerifier.setTrustedCertSources(trustedListsCertificateSource);

    // use the offline loader
    this.job.offlineRefresh();
  }

  /**
   * onlineLOTLRefresh refreshes the content of the lotl trust source by pulling new material from
   * the online lotl url
   */
  public void onlineLOTLRefresh() {
    this.job.onlineRefresh();
  }

  private CertificateVerifier initializeCertificateVerifier() {

    CommonCertificateVerifier certificateVerifier = new CommonCertificateVerifier();

    // Default configs
    certificateVerifier.setAlertOnMissingRevocationData(new ExceptionOnStatusAlert());
    certificateVerifier.setCheckRevocationForUntrustedChains(false);
    certificateVerifier.setDataLoader(new CommonsDataLoader());
    certificateVerifier.setCrlSource(new OnlineCRLSource());
    certificateVerifier.setOcspSource(new OnlineOCSPSource());

    return certificateVerifier;
  }

  private TLValidationJob initializeTLValidationJob() throws Exception {
    TLValidationJob job = new TLValidationJob();
    job.setListOfTrustedListSources(europeanLOTL());
    job.setOfflineDataLoader(offlineLoader());
    job.setOnlineDataLoader(onlineLoader());

    // set alerts
    job.setLOTLAlerts(Arrays.asList(ojUrlAlert(europeanLOTL()), lotlLocationAlert(europeanLOTL())));
    job.setTLAlerts(Arrays.asList(tlSigningAlert(), tlExpirationDetection()));

    return job;
  }

  private DSSFileLoader onlineLoader() throws Exception {

    FileCacheDataLoader onlineFileLoader = new FileCacheDataLoader();
    onlineFileLoader.setCacheExpirationTime(0);
    onlineFileLoader.setFileCacheDirectory(tlCacheDirectory());
    onlineFileLoader.setDataLoader(onlineDataLoader());

    return onlineFileLoader;
  }

  private CommonsDataLoader onlineDataLoader() throws Exception {

    // load the default java truststore
    KeyStore javaDefaultTruststore = Utils.getJavaDefaultTrustStore();

    // load the extra trust material truststore
    KeyStore extraKeystore =
        KeyStore.getInstance(this.validationProperties.getExtraTrustStoreType());
    InputStream extraIs =
        DocumentValidatorLOTL.class.getResourceAsStream(
            "/".concat(this.validationProperties.getExtraTrustStoreFile()));
    if (extraIs == null) {
      throw new FileNotFoundException(
          "Extra truststore "
              + this.validationProperties.getExtraTrustStoreFile()
              + " could not be loaded");
    }
    extraKeystore.load(
        extraIs, this.validationProperties.getExtraTrustStorePassword().toCharArray());
    // add the extra trust material to the java default one
    Enumeration<String> e = extraKeystore.aliases();
    while (e.hasMoreElements()) {
      String a = e.nextElement();
      javaDefaultTruststore.setEntry(a, extraKeystore.getEntry(a, null), null);
    }

    // convert the truststore to a byte output stream
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    javaDefaultTruststore.store(baos, "combined".toCharArray());

    // load the combined truststore to the online data loader used by the validation job
    CommonsDataLoader dataLoader = new CommonsDataLoader();
    dataLoader.setSslTruststore(new InMemoryDocument(new ByteArrayInputStream(baos.toByteArray())));
    dataLoader.setSslTruststoreType("JKS");
    dataLoader.setSslTruststorePassword("combined");

    return dataLoader;
  }

  private LOTLSource europeanLOTL() {
    LOTLSource lotlSource = new LOTLSource();
    lotlSource.setUrl(this.validationProperties.getLotlUrl());
    lotlSource.setCertificateSource(officialJournalKeystore());
    lotlSource.setSigningCertificatesAnnouncementPredicate(
        new OfficialJournalSchemeInformationURI(this.validationProperties.getOfficialJournalUrl()));
    lotlSource.setPivotSupport(true);
    return lotlSource;
  }

  private DSSFileLoader offlineLoader() {
    FileCacheDataLoader offlineFileLoader = new FileCacheDataLoader();
    offlineFileLoader.setCacheExpirationTime(Long.MAX_VALUE);
    offlineFileLoader.setDataLoader(new IgnoreDataLoader());
    offlineFileLoader.setFileCacheDirectory(tlCacheDirectory());
    return offlineFileLoader;
  }

  private File tlCacheDirectory() {
    File lotlCache = new File(this.validationProperties.getLotlCacheDir(), lotlCacheFolder);
    if (!lotlCache.exists()) {
      LOGGER.info(
          "LOTL cache {} doesn't exist.",
          lotlCache.getAbsolutePath(),
          f(ServiceLogField.builder().build()));
    }
    return lotlCache;
  }

  private KeyStoreCertificateSource officialJournalKeystore() {

    try {
      return new KeyStoreCertificateSource(
          this.getClass()
              .getResourceAsStream(
                  "/".concat(this.validationProperties.getOfficialJournalKeystoreFile())),
          this.validationProperties.getOfficialJournalKeystoreType(),
          this.validationProperties.getOfficialJournalKeystorePassword());
    } catch (Exception e) {
      throw new DSSException(
          "Unable to load the file " + this.validationProperties.getOfficialJournalKeystoreFile(),
          e);
    }
  }

  // LOTL Alerts

  private TLAlert tlSigningAlert() {
    TLSignatureErrorDetection signingDetection = new TLSignatureErrorDetection();
    LogTLSignatureErrorAlertHandler handler = new LogTLSignatureErrorAlertHandler();
    return new TLAlert(signingDetection, handler);
  }

  private TLAlert tlExpirationDetection() {
    TLExpirationDetection expirationDetection = new TLExpirationDetection();
    LogTLExpirationAlertHandler handler = new LogTLExpirationAlertHandler();
    return new TLAlert(expirationDetection, handler);
  }

  private LOTLAlert ojUrlAlert(LOTLSource source) {
    OJUrlChangeDetection ojUrlDetection = new OJUrlChangeDetection(source);
    LogOJUrlChangeAlertHandler handler = new LogOJUrlChangeAlertHandler();
    return new LOTLAlert(ojUrlDetection, handler);
  }

  private LOTLAlert lotlLocationAlert(LOTLSource source) {
    LOTLLocationChangeDetection lotlLocationDetection = new LOTLLocationChangeDetection(source);
    LogLOTLLocationChangeAlertHandler handler = new LogLOTLLocationChangeAlertHandler();
    return new LOTLAlert(lotlLocationDetection, handler);
  }
}
