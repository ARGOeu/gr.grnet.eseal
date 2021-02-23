package gr.grnet.eseal;

import gr.grnet.eseal.config.RemoteProviderProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class EsealApplicationTests {

	private RemoteProviderProperties remoteProviderProperties;

	@Autowired
	 EsealApplicationTests(RemoteProviderProperties remoteProviderProperties) {
		this.remoteProviderProperties = remoteProviderProperties;
	}

	@Test
	void testRemoteProviderPropertiesLoad() {
		assertThat("test.provider.com").isEqualTo(this.remoteProviderProperties.getEndpoint());
	}

	@Test
	void contextLoads() {
	}

}
