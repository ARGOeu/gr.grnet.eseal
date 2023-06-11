package gr.grnet.eseal;

import static org.assertj.core.api.Assertions.assertThat;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.pades.DSSFileFont;
import eu.europa.esig.dss.pades.DSSFont;
import gr.grnet.eseal.config.DocumentValidatorLOTLBean;
import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.config.ValidationProperties;
import gr.grnet.eseal.config.VisibleSignatureProperties;
import gr.grnet.eseal.config.VisibleSignaturePropertiesBean;
import gr.grnet.eseal.timestamp.DefaultTSASourceProperties;
import gr.grnet.eseal.timestamp.TSASourcePropertiesWithBasicAuth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class EsealApplicationTests {

  private RemoteProviderProperties remoteProviderProperties;

  private ValidationProperties validationProperties;

  private DocumentValidatorLOTLBean documentValidatorLOTLBean;

  private VisibleSignatureProperties visibleSignatureProperties;

  private DefaultTSASourceProperties apedTSPSourceProperties;

  private TSASourcePropertiesWithBasicAuth haricaTSPSourceProperties;

  @Autowired
  EsealApplicationTests(
      RemoteProviderProperties remoteProviderProperties,
      ValidationProperties validationProperties,
      DocumentValidatorLOTLBean documentValidatorLOTLBean,
      VisibleSignatureProperties visibleSignatureProperties,
      DefaultTSASourceProperties apedTSPSourceProperties,
      TSASourcePropertiesWithBasicAuth haricaTSPSourceProperties) {

    this.remoteProviderProperties = remoteProviderProperties;
    this.validationProperties = validationProperties;
    this.documentValidatorLOTLBean = documentValidatorLOTLBean;
    this.visibleSignatureProperties = visibleSignatureProperties;
    this.apedTSPSourceProperties = apedTSPSourceProperties;
    this.haricaTSPSourceProperties = haricaTSPSourceProperties;
  }

  @Test
  void testValidationProperties() {
    assertThat("oj.keystore.p12")
        .isEqualTo(this.validationProperties.getOfficialJournalKeystoreFile());
    assertThat("dss-password")
        .isEqualTo(this.validationProperties.getOfficialJournalKeystorePassword());
    assertThat("PKCS12").isEqualTo(this.validationProperties.getOfficialJournalKeystoreType());
    assertThat(
            "https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.2019.276.01.0001.01.ENG")
        .isEqualTo(this.validationProperties.getOfficialJournalUrl());
    assertThat("/tmp").isEqualTo(this.validationProperties.getLotlCacheDir());
    assertThat("extra-lotl.truststore.jks")
        .isEqualTo(this.validationProperties.getExtraTrustStoreFile());
    assertThat("extra-tl").isEqualTo(this.validationProperties.getExtraTrustStorePassword());
    assertThat("JKS").isEqualTo(this.validationProperties.getExtraTrustStoreType());
  }

  @Test
  void testApedTSPProperties() {
    assertThat("https://timestamp.aped.gov.gr/qtss")
        .isEqualTo(this.apedTSPSourceProperties.getUrl());
    assertThat("aped.truststore.jks")
        .isEqualTo(this.apedTSPSourceProperties.getTruststore().getFile());
    assertThat("apedts").isEqualTo(this.apedTSPSourceProperties.getTruststore().getPassword());
    assertThat("JKS").isEqualTo(this.apedTSPSourceProperties.getTruststore().getType());
  }

  @Test
  void testHaricaTSPProperties() {
    assertThat("https://qts.harica.gr").isEqualTo(this.haricaTSPSourceProperties.getUrl());
    assertThat("harica.truststore.jks")
        .isEqualTo(this.haricaTSPSourceProperties.getTruststore().getFile());
    assertThat("harica-qtsa")
        .isEqualTo(this.haricaTSPSourceProperties.getTruststore().getPassword());
    assertThat("JKS").isEqualTo(this.haricaTSPSourceProperties.getTruststore().getType());
    assertThat("qts.harica.gr").isEqualTo(this.haricaTSPSourceProperties.getHost());
    assertThat(443).isEqualTo(this.haricaTSPSourceProperties.getPort());
    assertThat("Basic").isEqualTo(this.haricaTSPSourceProperties.getScheme());
    assertThat("STE").isEqualTo(this.haricaTSPSourceProperties.getUsername());
    assertThat("xRJbYDn7tgP8mhIhdN6te3oDo5UUKJY7")
        .isEqualTo(this.haricaTSPSourceProperties.getPassword());
  }

  @Test
  void testRemoteProviderPropertiesLoad() {
    assertThat("rsign-api-dev.harica.gr").isEqualTo(this.remoteProviderProperties.getEndpoint());
    assertThat(false).isEqualTo(this.remoteProviderProperties.isRetryEnabled());
    assertThat(3).isEqualTo(this.remoteProviderProperties.getRetryCounter());
    assertThat(5).isEqualTo(this.remoteProviderProperties.getRetryInterval());
    assertThat(5).isEqualTo(this.remoteProviderProperties.getTotpWaitForRefreshSeconds());
    assertThat(true).isEqualTo(this.remoteProviderProperties.isTlsVerifyEnabled());
    assertThat("remote_provider_http_eseal_client.truststore.jks")
        .isEqualTo(this.remoteProviderProperties.getTruststoreFile());
    assertThat("providerpass").isEqualTo(this.remoteProviderProperties.getTruststorePassword());
    assertThat("JKS").isEqualTo(this.remoteProviderProperties.getTruststoreType());
    assertThat(60000).isEqualTo(this.remoteProviderProperties.getSocketConnectTimeout());
    assertThat(60000).isEqualTo(this.remoteProviderProperties.getConnectTimeout());
    assertThat(60000).isEqualTo(this.remoteProviderProperties.getRequestConnectTimeout());
  }

  @Test
  void testDocumentValidatorLOTLBean() {
    assertThat(true).isEqualTo(this.documentValidatorLOTLBean.getLotlRefreshEnable());
    assertThat(0).isEqualTo(this.documentValidatorLOTLBean.getRefreshInitialDelay());
    assertThat(21600000).isEqualTo(this.documentValidatorLOTLBean.getRefreshInterval());
  }

  @Test
  void testVisibleSignaturePropertiesLoad() {

    DSSDocument imageDocument =
        new InMemoryDocument(
            VisibleSignaturePropertiesBean.class.getResourceAsStream(
                "/visible-signature/".concat("osddydd.png")));

    assertThat(this.visibleSignatureProperties.getImageDocument().getDigest(DigestAlgorithm.SHA256))
        .isEqualTo(imageDocument.getDigest(DigestAlgorithm.SHA256));

    DSSFont font =
        new DSSFileFont(
            VisibleSignaturePropertiesBean.class.getResourceAsStream(
                "/visible-signature/".concat("DejaVuSans.ttf")));
    font.setSize(6);

    assertThat(this.visibleSignatureProperties.getFont().getSize()).isEqualTo(font.getSize());
    assertThat(this.visibleSignatureProperties.getFont().getJavaFont())
        .isEqualTo(font.getJavaFont());

    assertThat(this.visibleSignatureProperties.getZoneId()).isEqualTo(ZoneId.of("Europe/Athens"));

    assertThat(this.visibleSignatureProperties.getDateTimeFormatter().toString())
        .isEqualTo(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss z").toString());
  }

  @Test
  void contextLoads() {}
}
