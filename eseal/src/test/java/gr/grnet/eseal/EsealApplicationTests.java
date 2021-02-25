package gr.grnet.eseal;

import gr.grnet.eseal.config.DocumentValidatorLOTLBean;
import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.config.ValidationProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class EsealApplicationTests {

	private RemoteProviderProperties remoteProviderProperties;

	private ValidationProperties validationProperties;

	private DocumentValidatorLOTLBean documentValidatorLOTLBean;

	@Autowired
	 EsealApplicationTests(RemoteProviderProperties remoteProviderProperties,
						   ValidationProperties validationProperties,
						   DocumentValidatorLOTLBean documentValidatorLOTLBean) {

		this.remoteProviderProperties = remoteProviderProperties;
		this.validationProperties = validationProperties;
		this.documentValidatorLOTLBean = documentValidatorLOTLBean;
	}

	@Test
	void testValidationProperties() {
		assertThat("oj.keystore.p12").isEqualTo(this.validationProperties.getOfficialJournalKeystoreFile());
		assertThat("dss-password").isEqualTo(this.validationProperties.getOfficialJournalKeystorePassword());
		assertThat("PKCS12").isEqualTo(this.validationProperties.getOfficialJournalKeystoreType());
		assertThat("https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.2019.276.01.0001.01.ENG")
				.isEqualTo(this.validationProperties.getOfficialJournalUrl());
		assertThat("/etc/eseal/conf.d/").isEqualTo(this.validationProperties.getLotlCacheDir());
		assertThat("https://ec.europa.eu/tools/lotl/eu-lotl.xml").isEqualTo(this.validationProperties.getLotlUrl());
	}

	@Test
	void testRemoteProviderPropertiesLoad() {
		assertThat("test.provider.com").isEqualTo(this.remoteProviderProperties.getEndpoint());
		assertThat(true).isEqualTo(this.remoteProviderProperties.isRetryEnabled());
		assertThat(3).isEqualTo(this.remoteProviderProperties.getRetryCounter());
		assertThat(5).isEqualTo(this.remoteProviderProperties.getRetryInterval());
		assertThat(5).isEqualTo(this.remoteProviderProperties.getTotpWaitForRefreshSeconds());
		assertThat(true).isEqualTo(this.remoteProviderProperties.isTlsVerifyEnabled());
		assertThat("remote_provider_http_eseal_client.truststore.jks").isEqualTo(this.remoteProviderProperties.getTruststoreFile());
		assertThat("providerpass").isEqualTo(this.remoteProviderProperties.getTruststorePassword());
		assertThat("JKS").isEqualTo(this.remoteProviderProperties.getTruststoreType());
	}

	@Test
	void testDocumentValidatorLOTLBean() {
		assertThat(true).isEqualTo(this.documentValidatorLOTLBean.getLotlRefreshEnable());
		assertThat(0).isEqualTo(this.documentValidatorLOTLBean.getRefreshInitialDelay());
		assertThat(21600000).isEqualTo(this.documentValidatorLOTLBean.getRefreshInterval());
	}

	@Test
	void contextLoads() {
	}

}
