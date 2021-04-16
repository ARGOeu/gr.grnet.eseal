package gr.grnet.eseal.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@NoArgsConstructor
public class TSPSourceProperties {

  /** TSP url */
  @Value("${eseal.tsp.url}")
  private String tspURL;

  /** TSP truststore type */
  @Value("${eseal.tsp.truststore.type}")
  private String tspTruststoreType;

  /** TSP truststore file name */
  @Value("${eseal.tsp.truststore.file}")
  private String tspTruststoreFile;

  /** TSP truststore password */
  @Value("${eseal.tsp.truststore.password}")
  private String tspTruststorePassword;
}
