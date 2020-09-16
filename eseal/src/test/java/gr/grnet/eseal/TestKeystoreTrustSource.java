package gr.grnet.eseal;

import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestKeystoreTrustSource {

    @Test
    public void testX509CertificateTrustSourceFilePath() {
        try {
            KeystoreTrustSource keystoreTrustSource =
                    new KeystoreTrustSource(
                            TestX509CertificateTrustSource.class.getResource(
                                    "/trustsource/eseal.truststore.jks").getFile(),
                                "eseal12345",
                            KeyStoreType.JKS);

            CommonTrustedCertificateSource commonTrustedCertificateSource = keystoreTrustSource.getCommonTrustedCertificateSource();
            assertEquals("Number of added certificates" , 1 ,commonTrustedCertificateSource.getNumberOfCertificates());
            assertEquals("Certificate info",
                    "C=GR,L=Athens,O=Ministry of Digital Governance,2.5.4.97=VATGR-997001671,OU=Class B - Private Key created and stored in software CSP,CN=Ministry of Digital Governance\\, Hellenic Republic,E=sec@mindigital.gr",
                    commonTrustedCertificateSource.getCertificates().get(0).getCertificate().getSubjectDN().getName());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testX509CertificateTrustSourceFile() {
        try {
            KeystoreTrustSource keystoreTrustSource =
                    new KeystoreTrustSource(
                            new File(TestX509CertificateTrustSource.class.getResource(
                                    "/trustsource/eseal.truststore.jks").getFile()),
                            "eseal12345",
                            KeyStoreType.JKS);

            CommonTrustedCertificateSource commonTrustedCertificateSource = keystoreTrustSource.getCommonTrustedCertificateSource();
            assertEquals("Number of added certificates" , 1 ,commonTrustedCertificateSource.getNumberOfCertificates());
            assertEquals("Certificate info",
                    "C=GR,L=Athens,O=Ministry of Digital Governance,2.5.4.97=VATGR-997001671,OU=Class B - Private Key created and stored in software CSP,CN=Ministry of Digital Governance\\, Hellenic Republic,E=sec@mindigital.gr",
                    commonTrustedCertificateSource.getCertificates().get(0).getCertificate().getSubjectDN().getName());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test(expected = IOException.class)
    public void testX509CertificateTrustSourceFileNotFound() throws IOException {
            KeystoreTrustSource keystoreTrustSource =
                    new KeystoreTrustSource(
                            "/not/found",
                            "eseal12345",
                            KeyStoreType.JKS);

    }
}
