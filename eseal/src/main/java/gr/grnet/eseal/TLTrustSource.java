package gr.grnet.eseal;

import static gr.grnet.eseal.TrustedListsUtils.*;

import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CommonCertificateSource;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.TLSource;
import eu.europa.esig.dss.tsl.sync.AcceptAllStrategy;
import java.util.Arrays;

/**
 * <p>
 *     Trust source that will be supplied to the pdf validation process based on a trusted list.
 * </p>
 */
public class TLTrustSource {

    private TLValidationJob job;

    /** Creates a trusted list trust source.
     * @param trustedListURL enum of a trusted list url
     */
    public TLTrustSource(TrustedListURL trustedListURL) {
        this(trustedListURL.toString());
    }

    /** Creates a trusted list trust source from the provided trusted list.
     * @param url for the trusted list.
     */

    public TLTrustSource(String url) {
            this.job = this.buildSource(url);
    }

    public TLValidationJob getJob() {
        return job;
    }

    private TLValidationJob buildSource(String url) {
        TLValidationJob job = new TLValidationJob();
        job.setOfflineDataLoader(offlineLoader());
        job.setOnlineDataLoader(onlineLoader());
        job.setTrustedListCertificateSource(new TrustedListsCertificateSource());
        job.setSynchronizationStrategy(new AcceptAllStrategy());
        job.setCacheCleaner(cacheCleaner());

        TLSource tlSource = new TLSource();
        tlSource.setUrl(url);
        tlSource.setCertificateSource(new CommonCertificateSource());
        job.setTrustedListSources(tlSource);
        job.setTLAlerts(Arrays.asList(tlSigningAlert(), tlExpirationDetection()));

        return job;
    }
}

