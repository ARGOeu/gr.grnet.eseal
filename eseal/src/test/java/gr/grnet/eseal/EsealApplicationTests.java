package gr.grnet.eseal;

import gr.grnet.eseal.config.HaricaProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class EsealApplicationTests {

	private HaricaProperties haricaProperties;

	@Autowired
	 EsealApplicationTests(HaricaProperties haricaProperties) {
		this.haricaProperties = haricaProperties;
	}

	@Test
	void testHaricaPropertiesLoad() {
		assertThat("test.harica.com").isEqualTo(this.haricaProperties.getEndpoint());
		assertThat("test-user").isEqualTo(this.haricaProperties.getUsername());
		assertThat("test-password").isEqualTo(this.haricaProperties.getPassword());
		assertThat("test-key").isEqualTo(this.haricaProperties.getKey());
	}

	@Test
	void contextLoads() {
	}

}
