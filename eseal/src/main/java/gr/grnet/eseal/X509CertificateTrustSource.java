package gr.grnet.eseal;

import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * <p>
 *     Trust source that will be supplied to the pdf validation process based on a x509 certificate source.
 * </p>
 */
public class X509CertificateTrustSource {

    private X509Certificate cert;
    private CommonTrustedCertificateSource commonTrustedCertificateSource;

    /** Creates an x509 certificate trust source validator from the given certificate(path to certificate).
     * @param filepath path to the cert file.
     */
    public X509CertificateTrustSource(String filepath) throws FileNotFoundException, CertificateException {
            InputStream inStream = new FileInputStream(filepath);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            this.cert = (X509Certificate)cf.generateCertificate(inStream);
            this.buildSource();
    }

    /** Creates an x509 certificate trust source validator from the given certificate.
     * @param cert X509 certificate
     */
    public X509CertificateTrustSource(X509Certificate cert) {
        this.cert = cert;
        this.buildSource();
    }

    public CommonTrustedCertificateSource getCommonTrustedCertificateSource() {
        return commonTrustedCertificateSource;
    }

    /**
     * Builds the dss common trusted certificate source with the present x509 cert
     */
    private void buildSource() {
        CommonTrustedCertificateSource ctsf = new CommonTrustedCertificateSource();
        CertificateToken certificateToken = new CertificateToken(this.cert);
        ctsf.addCertificate(certificateToken);
        this.commonTrustedCertificateSource = ctsf;
    }

}
