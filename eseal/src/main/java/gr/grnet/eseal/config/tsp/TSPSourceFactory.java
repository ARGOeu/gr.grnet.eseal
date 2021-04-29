package gr.grnet.eseal.config.tsp;

import static net.logstash.logback.argument.StructuredArguments.f;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.service.SecureRandomNonceSource;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import gr.grnet.eseal.logging.ServiceLogField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TSPSourceFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(TSPSourceFactory.class);

  private final TSPSourcePropertiesFactory tspSourcePropertiesFactory;

  @Autowired
  public TSPSourceFactory(TSPSourcePropertiesFactory tspSourcePropertiesFactory) {
    this.tspSourcePropertiesFactory = tspSourcePropertiesFactory;
  }

  public TSPSource createTspSource(TSPSourceEnum tspSourceEnum) {

    TSPSourceProperties tspSourceProperties =
        tspSourcePropertiesFactory.getTSPSourceProperties(tspSourceEnum);

    OnlineTSPSource tsp = new OnlineTSPSource(tspSourceProperties.getUrl());
    tsp.setNonceSource(new SecureRandomNonceSource());

    // uses the specific content-type
    TimestampDataLoader timestampDataLoader = new TimestampDataLoader();

    LOGGER.info("Building tsp truststore", f(ServiceLogField.builder().build()));

    DSSDocument tspTruststoreFile =
        new InMemoryDocument(
            TSPSourceFactory.class.getResourceAsStream(
                "/".concat(tspSourceProperties.getTruststore().getFile())));
    timestampDataLoader.setSslTruststore(tspTruststoreFile);
    timestampDataLoader.setSslTruststorePassword(tspSourceProperties.getTruststore().getPassword());
    timestampDataLoader.setSslTruststoreType(tspSourceProperties.getTruststore().getType());
    tsp.setDataLoader(timestampDataLoader);
    return tsp;
  }
}
