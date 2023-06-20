package gr.grnet.eseal.service;

import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class TrustedCertSourcesBean {

  @Value("${eseal.manual.truststore.file}")
  private String truststoreFile;

  @Value("${eseal.manual.truststore.password}")
  private String truststorePassword;

  @Autowired private ResourceLoader resourceLoader;

  private CommonTrustedCertificateSource certificateSource;

  public CommonTrustedCertificateSource getSource() throws IOException {
    if (certificateSource == null) {
      certificateSource = createSource();
    }

    return certificateSource;
  }

  private CommonTrustedCertificateSource createSource() throws IOException {
    CommonTrustedCertificateSource source = null;
    Resource resource = resourceLoader.getResource(truststoreFile);
    try (InputStream is = resource.getInputStream()) {
      KeyStoreCertificateSource keystore =
          new KeyStoreCertificateSource(is, "JKS", truststorePassword);
      source = new CommonTrustedCertificateSource();
      source.importAsTrusted(keystore);
    }

    return source;
  }
}
