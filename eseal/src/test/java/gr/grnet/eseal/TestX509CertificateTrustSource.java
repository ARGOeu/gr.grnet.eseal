package gr.grnet.eseal;

import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class TestX509CertificateTrustSource {

    @Test
    public void testX509CertificateTrustSourceFile() {
        try {
            X509CertificateTrustSource x509CertificateTrustSource =
                    new X509CertificateTrustSource(TestX509CertificateTrustSource.class.getResource("/x509source/x509CA.cer").getFile());

            CommonTrustedCertificateSource commonTrustedCertificateSource = x509CertificateTrustSource.getCommonTrustedCertificateSource();
            assertEquals("Number of added certificates" , 1 ,commonTrustedCertificateSource.getNumberOfCertificates());
            assertEquals("Certificate info",
                    "EMAILADDRESS=sec@mindigital.gr, CN=\"Ministry of Digital Governance, Hellenic Republic\", OU=Class B - Private Key created and stored in software CSP, OID.2.5.4.97=VATGR-997001671, O=Ministry of Digital Governance, L=Athens, C=GR",
                    commonTrustedCertificateSource.getCertificates().get(0).getCertificate().getSubjectDN().getName());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testX509CertificateTrustSourceCERTObj() {

        X509Certificate cert = null;

        try {
            InputStream inStream = new FileInputStream(TestX509CertificateTrustSource.class.getResource("/x509source/x509CA.cer").getFile());
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(inStream);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        X509CertificateTrustSource x509CertificateTrustSource = new X509CertificateTrustSource(cert);

        CommonTrustedCertificateSource commonTrustedCertificateSource = x509CertificateTrustSource.getCommonTrustedCertificateSource();
        assertEquals("Number of added certificates", 1, commonTrustedCertificateSource.getNumberOfCertificates());
        assertEquals("Certificate info",
                        "EMAILADDRESS=sec@mindigital.gr, CN=\"Ministry of Digital Governance, Hellenic Republic\", OU=Class B - Private Key created and stored in software CSP, OID.2.5.4.97=VATGR-997001671, O=Ministry of Digital Governance, L=Athens, C=GR",
                commonTrustedCertificateSource.getCertificates().get(0).getCertificate().getSubjectDN().getName());
    }

    @Test(expected = FileNotFoundException.class)
    public void testX509CertificateTrustSourceNotFound() throws Exception{
        try {
            X509CertificateTrustSource x509CertificateTrustSource = new X509CertificateTrustSource("/not/found");
        } catch (Exception e) {
            throw e;
        }
    }

    @Test(expected = CertificateException.class)
    public void testX509CertificateTrustSourceNoCert() throws Exception{
        try {
            X509CertificateTrustSource x509CertificateTrustSource = new X509CertificateTrustSource(TestX509CertificateTrustSource.class.getResource("/declaration.pdf").getFile());
        } catch (Exception e) {
            throw e;
        }
    }
}
