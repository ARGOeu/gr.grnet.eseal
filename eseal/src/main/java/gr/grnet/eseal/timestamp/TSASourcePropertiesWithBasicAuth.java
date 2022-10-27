package gr.grnet.eseal.timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TSASourcePropertiesWithBasicAuth extends TSASourceProperties {

  /** TSA scheme */
  private String scheme = "Basic";

  /** TSA host */
  private String host;

  /** TSA Basic Authentication username */
  private String username;

  /** TSA Basic Authentication password */
  private String password;

  /** TSA port */
  private int port = 443;
}
