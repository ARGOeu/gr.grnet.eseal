package gr.grnet.eseal.service;

import static net.logstash.logback.argument.StructuredArguments.f;

import eu.europa.esig.dss.alert.ExceptionOnStatusAlert;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.pades.PAdESTimestampParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import gr.grnet.eseal.config.tsp.TSPSourceEnum;
import gr.grnet.eseal.config.tsp.TSPSourceFactory;
import gr.grnet.eseal.exception.InternalServerErrorException;
import gr.grnet.eseal.logging.ServiceLogField;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimestampDocumentService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TimestampDocumentService.class);

  private final TSPSourceFactory tspSourceFactory;

  @Autowired
  public TimestampDocumentService(TSPSourceFactory tspSourceFactory) {
    this.tspSourceFactory = tspSourceFactory;
  }

  public String timestampDocument(String document, TSPSourceEnum tspSourceEnum) {

    DSSDocument documentToTimestamp = new InMemoryDocument(Utils.fromBase64(document));

    CommonCertificateVerifier certificateVerifier = new CommonCertificateVerifier();

    // Default configs
    certificateVerifier.setAlertOnMissingRevocationData(new ExceptionOnStatusAlert());
    certificateVerifier.setCheckRevocationForUntrustedChains(false);
    certificateVerifier.setDataLoader(new TimestampDataLoader());
    certificateVerifier.setCrlSource(new OnlineCRLSource());
    certificateVerifier.setOcspSource(new OnlineOCSPSource());

    // Configure a PAdES service for PDF timestamping
    PAdESService service = new PAdESService(certificateVerifier);
    service.setTspSource(tspSourceFactory.createTspSource(tspSourceEnum));

    String timestampedDocumentB64;

    // Execute the timestamp method
    try {
      DSSDocument timestampedDoc =
          service.timestamp(
              documentToTimestamp, new PAdESTimestampParameters(DigestAlgorithm.SHA256));
      timestampedDocumentB64 = Utils.toBase64(Utils.toByteArray(timestampedDoc.openStream()));
    } catch (IOException e) {
      LOGGER.error(
          "Error converting timestamped pdf to base64",
          f(ServiceLogField.builder().details(e.getMessage()).build()));
      throw new InternalServerErrorException("Could not produce timestamped document");

    } catch (DSSException de) {
      LOGGER.error(
          "DSS Error while timestamping document",
          f(ServiceLogField.builder().details(de.getMessage()).build()));
      throw new InternalServerErrorException("Could not timestamp document");
    }

    return timestampedDocumentB64;
  }
}
