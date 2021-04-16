package gr.grnet.eseal.utils;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import org.apache.commons.lang3.time.DurationFormatUtils;

public final class Utils {

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
   * @param subject pf an x509 certificate
   * @return the value of the common name rdn
   * @throws Exception when the subject is invalid
   */
  public static String extractCNFromSubject(String subject) throws Exception {

    LdapName ldapName = new LdapName(subject);

    for (Rdn rdn : ldapName.getRdns()) {
      if (rdn.getType().equals("CN")) {
        return rdn.getValue().toString();
      }
    }
    throw new Exception("No Common Name present in subject");
  }
}
