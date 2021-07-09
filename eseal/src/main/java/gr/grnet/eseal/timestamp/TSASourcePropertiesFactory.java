package gr.grnet.eseal.timestamp;

import gr.grnet.eseal.enums.TSASourceEnum;
import gr.grnet.eseal.exception.APIException;
import org.springframework.http.HttpStatus;

public class TSASourcePropertiesFactory {

  private final DefaultTSASourceProperties apedTSASourceProperties;
  private final TSASourcePropertiesWithBasicAuth haricaTSASourceProperties;

  public TSASourcePropertiesFactory(
      DefaultTSASourceProperties apedTSASourceProperties,
      TSASourcePropertiesWithBasicAuth haricaTSASourceProperties) {
    this.apedTSASourceProperties = apedTSASourceProperties;
    this.haricaTSASourceProperties = haricaTSASourceProperties;
  }

  public TSASourceProperties getTSASourceProperties(TSASourceEnum tsaSourceEnum) {

    switch (tsaSourceEnum) {
      case APED:
        return apedTSASourceProperties;
      case HARICA:
        return haricaTSASourceProperties;
      default:
        throw new APIException(
            HttpStatus.BAD_REQUEST.value(), "Unknown tsa source", HttpStatus.BAD_REQUEST);
    }
  }
}
