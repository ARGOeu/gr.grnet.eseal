package gr.grnet.eseal;

import static gr.grnet.eseal.TrustedListsUtils.*;

import eu.europa.esig.dss.spi.x509.CommonCertificateSource;
import eu.europa.esig.dss.tsl.function.OfficialJournalSchemeInformationURI;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.tsl.sync.AcceptAllStrategy;
import java.util.Arrays;

/**
 * <p>
 *     Trust source that will be supplied to the pdf validation process based on a list of trusted lists.
 * </p>
 */
public class LOTLTrustSource {

    private TLValidationJob job;
    private static final String OJ_URL = "https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.2019.276.01.0001.01.ENG";

    /** Creates a trusted list trust source from the provided list of trusted lists.
     * @param lotlurl enum of a lotl url
     */
    public LOTLTrustSource(LOTLURL lotlurl) {
        this(lotlurl.toString());
    }

    /** Creates a trusted list trust source from the provided list of trusted trusted lists.
     * @param url for the list of trusted lists.
     */
    public LOTLTrustSource(String url) {
        this.job = this.buildSource(url);
    }

    public TLValidationJob getJob() {
        return job;
    }

    private TLValidationJob buildSource(String url){
            TLValidationJob job = new TLValidationJob();
            job.setOfflineDataLoader(offlineLoader());
            job.setOnlineDataLoader(onlineLoader());
            job.setSynchronizationStrategy(new AcceptAllStrategy());
            job.setCacheCleaner(cacheCleaner());

            LOTLSource lotlSource = new LOTLSource();
            lotlSource.setUrl(url);
            lotlSource.setCertificateSource(new CommonCertificateSource());
            lotlSource.setSigningCertificatesAnnouncementPredicate(new OfficialJournalSchemeInformationURI(OJ_URL));
            lotlSource.setPivotSupport(true);;

            job.setListOfTrustedListSources(lotlSource);
            job.setLOTLAlerts(Arrays.asList(ojUrlAlert(lotlSource), lotlLocationAlert(lotlSource)));
            job.setTLAlerts(Arrays.asList(tlSigningAlert(), tlExpirationDetection()));
            return job;
    }
}
