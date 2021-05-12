package gr.grnet.eseal.timestamp;

import static net.logstash.logback.argument.StructuredArguments.f;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.service.SecureRandomNonceSource;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import gr.grnet.eseal.logging.ServiceLogField;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TSASourceRegistry {

  private static final Logger LOGGER = LoggerFactory.getLogger(TSASourceRegistry.class);

  private HashMap<TSASourceEnum, TSPSource> tsaSources;

  public TSASourceRegistry(TSASourcePropertiesFactory tspSourcePropertiesFactory) {

    this.tsaSources = new HashMap<TSASourceEnum, TSPSource>();

    for (TSASourceEnum tsaSourceEnum : TSASourceEnum.values()) {

      TSASourceProperties tspSourceProperties =
          tspSourcePropertiesFactory.getTSASourceProperties(tsaSourceEnum);

      LOGGER.info(
          "Initializing " + tsaSourceEnum.name() + " TSA . . .",
          f(ServiceLogField.builder().build()));

      this.tsaSources.put(tsaSourceEnum, createTSPSource(tspSourceProperties));
    }
  }

  public TSPSource getTSASource(TSASourceEnum tsaSourceEnum) {
    return this.tsaSources.get(tsaSourceEnum);
  }

  private TSPSource createTSPSource(TSASourceProperties tspSourceProperties) {

    OnlineTSPSource tsp = new OnlineTSPSource(tspSourceProperties.getUrl());
    tsp.setNonceSource(new SecureRandomNonceSource());
    if (tspSourceProperties instanceof TSASourcePropertiesWithBasicAuth) {
      tsp.setDataLoader(
          createTimestampDataLoader((TSASourcePropertiesWithBasicAuth) tspSourceProperties));
    } else if (tspSourceProperties instanceof DefaultTSASourceProperties) {
      tsp.setDataLoader(
          createTimestampDataLoader((DefaultTSASourceProperties) tspSourceProperties));
    }
    return tsp;
  }

  private TimestampDataLoader createTimestampDataLoader(
      TSASourcePropertiesWithBasicAuth tsaSourcePropertiesWithBasicAuth) {

    TimestampDataLoader timestampDataLoader =
        dataLoader(
            tsaSourcePropertiesWithBasicAuth.getTruststore().getFile(),
            tsaSourcePropertiesWithBasicAuth.getTruststore().getPassword(),
            tsaSourcePropertiesWithBasicAuth.getTruststore().getType());

    timestampDataLoader.addAuthentication(
        tsaSourcePropertiesWithBasicAuth.getHost(),
        tsaSourcePropertiesWithBasicAuth.getPort(),
        tsaSourcePropertiesWithBasicAuth.getScheme(),
        tsaSourcePropertiesWithBasicAuth.getUsername(),
        tsaSourcePropertiesWithBasicAuth.getPassword());

    return timestampDataLoader;
  }

  private TimestampDataLoader createTimestampDataLoader(
      DefaultTSASourceProperties defaultTSASourceProperties) {

    return dataLoader(
        defaultTSASourceProperties.getTruststore().getFile(),
        defaultTSASourceProperties.getTruststore().getPassword(),
        defaultTSASourceProperties.getTruststore().getType());
  }

  private TimestampDataLoader dataLoader(String truststore, String password, String type) {
    TimestampDataLoader timestampDataLoader = new TimestampDataLoader();

    LOGGER.info("Building TSA truststore . . .", f(ServiceLogField.builder().build()));

    DSSDocument tspTruststoreFile =
        new InMemoryDocument(TSASourceRegistry.class.getResourceAsStream("/".concat(truststore)));
    timestampDataLoader.setSslTruststore(tspTruststoreFile);
    timestampDataLoader.setSslTruststorePassword(password);
    timestampDataLoader.setSslTruststoreType(type);

    return timestampDataLoader;
  }
}
