package gr.grnet.eseal.config.tsp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
class TSPSourceProperties {

  /** TSP url */
  private String url;

  private final Truststore truststore = new Truststore();

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Truststore {

    /** TSP truststore type */
    private String type;

    /** TSP truststore file name */
    private String file;

    /** TSP truststore password */
    private String password;
  }
}
