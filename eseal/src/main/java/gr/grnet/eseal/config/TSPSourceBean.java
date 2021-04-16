package gr.grnet.eseal.config;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.service.SecureRandomNonceSource;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TSPSourceBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(TSPSourceBean.class);

  private TSPSourceProperties tspSourceProperties;

  @Autowired
  public TSPSourceBean(TSPSourceProperties tspSourceProperties) {
    this.tspSourceProperties = tspSourceProperties;
  }

  @Bean
  public TSPSource tspSource() {
    OnlineTSPSource tsp = new OnlineTSPSource(this.tspSourceProperties.getTspURL());
    tsp.setNonceSource(new SecureRandomNonceSource());

    // uses the specific content-type
    TimestampDataLoader timestampDataLoader = new TimestampDataLoader();

    try {
      LOGGER.info("building tsp truststore");

      DSSDocument tspTruststoreFile =
          new InMemoryDocument(
              TSPSourceBean.class.getResourceAsStream(
                  "/".concat(this.tspSourceProperties.getTspTruststoreFile())));
      timestampDataLoader.setSslTruststore(tspTruststoreFile);
      timestampDataLoader.setSslTruststorePassword(
          this.tspSourceProperties.getTspTruststorePassword());
      timestampDataLoader.setSslTruststoreType(this.tspSourceProperties.getTspTruststoreType());
    } catch (Exception e) {
      LOGGER.error("Could not load tsp truststore " + e.getMessage());
    }
    tsp.setDataLoader(timestampDataLoader);
    return tsp;
  }
}
