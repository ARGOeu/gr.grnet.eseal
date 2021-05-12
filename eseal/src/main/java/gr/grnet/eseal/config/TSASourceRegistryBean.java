package gr.grnet.eseal.config;

import gr.grnet.eseal.timestamp.TSASourcePropertiesFactory;
import gr.grnet.eseal.timestamp.TSASourceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TSASourceRegistryBean {

  private HaricaTSASourceProperties haricaTSASourceProperties;

  private ApedTSASourceProperties apedTSASourceProperties;

  @Autowired
  public TSASourceRegistryBean(
      HaricaTSASourceProperties haricaTSASourceProperties,
      ApedTSASourceProperties apedTSASourceProperties) {
    this.haricaTSASourceProperties = haricaTSASourceProperties;
    this.apedTSASourceProperties = apedTSASourceProperties;
  }

  @Bean
  public TSASourceRegistry tsaSourceRegistry() {
    return new TSASourceRegistry(
        new TSASourcePropertiesFactory(
            this.apedTSASourceProperties, this.haricaTSASourceProperties));
  }
}
