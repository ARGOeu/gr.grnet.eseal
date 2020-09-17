package gr.grnet.eseal;

import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import java.io.File;
import java.io.IOException;

/**
 * <p>
 *     Trust source that will be supplied to the pdf validation process based on a Java keystore.
 * </p>
 */
public class KeystoreTrustSource {

    private KeyStoreCertificateSource truststore;
    private CommonTrustedCertificateSource commonTrustedCertificateSource;

    /** Creates a keystore trust source validator from the given keystore(path to keystore).
     * @param filepath path to the keystore file.
     * @param password password for the keystore
     * @param type of the keystore
     */
    public KeystoreTrustSource(String filepath, String password, KeyStoreType type) throws IOException{
        this.truststore = new KeyStoreCertificateSource(filepath, type.name(), password);
        this.buildSource();
    }

    /** Creates a keystore trust source validator from the given keystore(path to keystore).
     * @param file representing the keystore file.
     * @param password password for the keystore
     * @param type of the keystore
     */
    public KeystoreTrustSource(File file, String password, KeyStoreType type) throws IOException{
        this.truststore = new KeyStoreCertificateSource(file, type.name(), password);
        this.buildSource();
    }

    /**
     * Builds the dss common trusted certificate source with the present keystore
     */
    private void buildSource() {
        this.commonTrustedCertificateSource = new CommonTrustedCertificateSource();
        this.commonTrustedCertificateSource.importAsTrusted(this.truststore);
    }

    public CommonTrustedCertificateSource getCommonTrustedCertificateSource() {
        return commonTrustedCertificateSource;
    }

    public KeyStoreCertificateSource getTruststore() {
        return truststore;
    }
}

