package gr.grnet.eseal;

import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CommonCertificateSource;
import eu.europa.esig.dss.tsl.alerts.TLAlert;
import eu.europa.esig.dss.tsl.alerts.detections.TLExpirationDetection;
import eu.europa.esig.dss.tsl.alerts.detections.TLSignatureErrorDetection;
import eu.europa.esig.dss.tsl.alerts.handlers.log.LogTLExpirationAlertHandler;
import eu.europa.esig.dss.tsl.alerts.handlers.log.LogTLSignatureErrorAlertHandler;
import eu.europa.esig.dss.tsl.cache.CacheCleaner;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.TLSource;
import eu.europa.esig.dss.tsl.sync.AcceptAllStrategy;
import java.io.File;
import java.util.Arrays;

/**
 * <p>
 *     Trust source that will be supplied to the pdf validation process based on a trusted list.
 * </p>
 */
public class TLTrustSource {

    private TLValidationJob job;

    /** Creates a trusted list trust source.
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


    private static DSSFileLoader onlineLoader() {
        FileCacheDataLoader onlineFileLoader = new FileCacheDataLoader();
        onlineFileLoader.setCacheExpirationTime(100);
        onlineFileLoader.setDataLoader(new CommonsDataLoader());
        onlineFileLoader.setFileCacheDirectory(tlCacheDirectory());

        return onlineFileLoader;
    }

    private static File tlCacheDirectory() {
        File rootFolder = new File(System.getProperty("java.io.tmpdir"));
        File tslCache = new File(rootFolder, "dss-tsl-loader2");
        if (tslCache.mkdirs()) {
            System.out.println(tslCache.getAbsolutePath());
        }
        return tslCache;
    }

    private static TLAlert tlSigningAlert() {
        TLSignatureErrorDetection signingDetection = new TLSignatureErrorDetection();
        LogTLSignatureErrorAlertHandler handler = new LogTLSignatureErrorAlertHandler();
        return new TLAlert(signingDetection, handler);
    }

    private static TLAlert tlExpirationDetection() {
        TLExpirationDetection expirationDetection = new TLExpirationDetection();
        LogTLExpirationAlertHandler handler = new LogTLExpirationAlertHandler();
        return new TLAlert(expirationDetection, handler);
    }

    private static CacheCleaner cacheCleaner() {
        CacheCleaner cacheCleaner = new CacheCleaner();
        cacheCleaner.setCleanFileSystem(true);
        cacheCleaner.setCleanMemory(true);
        cacheCleaner.setDSSFileLoader(offlineLoader());
        return cacheCleaner;
    }

    private static DSSFileLoader offlineLoader() {
        FileCacheDataLoader offlineFileLoader = new FileCacheDataLoader();
        offlineFileLoader.setDataLoader(new IgnoreDataLoader());
        offlineFileLoader.setCacheExpirationTime(100);
        offlineFileLoader.setFileCacheDirectory(tlCacheDirectory());
        return offlineFileLoader;
    }

}

