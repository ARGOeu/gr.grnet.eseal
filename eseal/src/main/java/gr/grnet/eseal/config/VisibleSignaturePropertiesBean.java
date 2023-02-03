package gr.grnet.eseal.config;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.MimeType;
import eu.europa.esig.dss.pades.DSSFileFont;
import eu.europa.esig.dss.pades.DSSFont;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@NoArgsConstructor
public class VisibleSignaturePropertiesBean {

  @Bean
  public VisibleSignatureProperties visibleSignatureProperties() {

    VisibleSignatureProperties visibleSignatureProperties = new VisibleSignatureProperties();

    // load the default image
    DSSDocument imageDocument =
        new InMemoryDocument(
            VisibleSignaturePropertiesBean.class.getResourceAsStream(
                "/visible-signature/".concat("osddydd.png")));
    imageDocument.setMimeType(MimeType.PNG);
    imageDocument.setName("osddydd.png");

    visibleSignatureProperties.setImageDocument(imageDocument);

    // load the DejaVuSans font
    DSSFont font =
        new DSSFileFont(
            VisibleSignaturePropertiesBean.class.getResourceAsStream(
                "/visible-signature/".concat("DejaVuSans.ttf")));
    font.setSize(6);

    visibleSignatureProperties.setFont(font);

    return visibleSignatureProperties;
  }
}
