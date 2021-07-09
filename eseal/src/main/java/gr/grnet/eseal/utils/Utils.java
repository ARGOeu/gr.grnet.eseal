package gr.grnet.eseal.utils;

import static net.logstash.logback.argument.StructuredArguments.f;

import gr.grnet.eseal.logging.ServiceLogField;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Utils {

  private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

  private Utils() {
    // not called
  }

  /**
   * {@link #formatTimePeriod(long)} accepts the beginning (unix time) of period and formats its
   * duration in seconds, minutes, hours and days
   *
   * @param start
   * @return
   */
  public static String formatTimePeriod(final long start) {
    long currentTime = System.currentTimeMillis();
    long timePeriod = currentTime - start;
    String timePeriodToString = DurationFormatUtils.formatDuration(timePeriod, "d") + "d";
    if ("0d".equals(timePeriodToString)) {
      timePeriodToString = DurationFormatUtils.formatDuration(timePeriod, "H") + "h";
      if ("0h".equals(timePeriodToString)) {
        timePeriodToString = DurationFormatUtils.formatDuration(timePeriod, "m") + "m";
        if ("0m".equals(timePeriodToString)) {
          timePeriodToString = DurationFormatUtils.formatDuration(timePeriod, "s") + "s";
          if ("0s".equals(timePeriodToString)) {
            timePeriodToString = timePeriod + "ms";
          }
        }
      }
    }
    return timePeriodToString;
  }

  /**
   * * extractCNFromSubject extracts and returns the value of the common name rdn
   *
   * @param subject of an x509 certificate
   * @return the value of the common name rdn
   * @throws Exception when the subject is invalid
   */
  public static String extractCNFromSubject(String subject) throws Exception {
    return extractRDNFromSubject(subject, "CN");
  }

  /**
   * * extractCNFromSubject extracts and returns the value of the common name rdn
   *
   * @param subject of an x509 certificate
   * @return the value of the organizational unit rdn
   * @throws Exception when the subject is invalid
   */
  public static String extractOUFromSubject(String subject) throws Exception {
    return extractRDNFromSubject(subject, "OU");
  }

  private static String extractRDNFromSubject(String subject, String rdn) throws Exception {
    LdapName ldapName = new LdapName(subject);
    for (Rdn r : ldapName.getRdns()) {
      if (r.getType().equals(rdn)) {
        return r.getValue().toString();
      }
    }
    throw new Exception("No" + rdn + "present in subject");
  }

  /**
   * @param signerInfo represents the singer's info inside the signature
   * @param date represents the visible date of the signature
   * @return properly formatted and combined singerInfo and date
   */
  public static String formatVisibleSignatureText(String signerInfo, String date) {

    String signerInfoFormatted = String.format("%1$-100s", "Digitally signed by " + signerInfo);

    String dateFormatted = String.format("%1$-100s", "Date: " + date);

    return signerInfoFormatted + "\n" + dateFormatted;
  }

  /**
   * Loads the default java CA certs(generally, from JAVA_HOME/lib/cacerts) into a
   * keystore(truststore)
   *
   * @return the truststore that contains the default java CA certs
   * @throws Exception during the initialization of the trust material
   */
  public static KeyStore getJavaDefaultTrustStore() throws Exception {

    KeyStore trustStore = KeyStore.getInstance("JKS");
    trustStore.load(null, null);

    TrustManagerFactory trustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init((KeyStore) null);

    LOGGER.info("Loading default java truststore . . .", f(ServiceLogField.builder().build()));

    for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
      if (trustManager instanceof X509TrustManager) {
        for (X509Certificate acceptedIssuer :
            ((X509TrustManager) trustManager).getAcceptedIssuers()) {
          trustStore.setCertificateEntry(acceptedIssuer.getSubjectDN().getName(), acceptedIssuer);
          LOGGER.info(
              "Loaded certificate: {}",
              acceptedIssuer.getSubjectDN().getName(),
              f(ServiceLogField.builder().build()));
        }
      }
    }

    LOGGER.info("Loaded {} certificates", trustStore.size(), f(ServiceLogField.builder().build()));
    return trustStore;
  }
}
