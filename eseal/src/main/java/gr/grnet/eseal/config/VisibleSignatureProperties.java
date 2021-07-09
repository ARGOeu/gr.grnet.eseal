package gr.grnet.eseal.config;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.pades.DSSFont;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VisibleSignatureProperties {

  private DSSFont font;

  private DSSDocument imageDocument;

  private DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss z");

  private ZoneId zoneId = ZoneId.of("Europe/Athens");
}
