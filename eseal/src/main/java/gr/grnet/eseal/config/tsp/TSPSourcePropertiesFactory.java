package gr.grnet.eseal.config.tsp;

import gr.grnet.eseal.exception.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
class TSPSourcePropertiesFactory {

  private final ApedTSPSourceProperties apedTSPSourceProperties;
  private final HaricaTSPSourceProperties haricaTSPSourceProperties;

  @Autowired
  public TSPSourcePropertiesFactory(
      ApedTSPSourceProperties apedTSPSourceProperties,
      HaricaTSPSourceProperties haricaTSPSourceProperties) {
    this.apedTSPSourceProperties = apedTSPSourceProperties;
    this.haricaTSPSourceProperties = haricaTSPSourceProperties;
  }

  public TSPSourceProperties getTSPSourceProperties(TSPSourceEnum tspSourceEnum) {

    switch (tspSourceEnum) {
      case APED:
        return apedTSPSourceProperties;
      case HARICA:
        throw new APIException(
            HttpStatus.BAD_REQUEST.value(),
            "HARICA's timestamp server currently is not supported",
            HttpStatus.BAD_REQUEST);
      default:
        throw new APIException(
            HttpStatus.BAD_REQUEST.value(), "Unknown tsp source", HttpStatus.BAD_REQUEST);
    }
  }
}
