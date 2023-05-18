package gr.grnet.eseal.timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
class TSASourceProperties {

  /** TSA url */
  private String url;

  private final Truststore truststore = new Truststore();

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Truststore {

    /** TSA truststore type */
    private String type;

    /** TSA truststore file name */
    private String file;

    /** TSA truststore password */
    private String password;
  }

  /** indicates if this TSA should be used as primary source for timestamping */
  private boolean primary = false;
}
