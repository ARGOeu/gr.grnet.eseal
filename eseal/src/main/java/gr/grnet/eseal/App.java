package gr.grnet.eseal;


import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CommonCertificateSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.tsl.alerts.LOTLAlert;
import eu.europa.esig.dss.tsl.alerts.TLAlert;
import eu.europa.esig.dss.tsl.alerts.detections.LOTLLocationChangeDetection;
import eu.europa.esig.dss.tsl.alerts.detections.OJUrlChangeDetection;
import eu.europa.esig.dss.tsl.alerts.detections.TLExpirationDetection;
import eu.europa.esig.dss.tsl.alerts.detections.TLSignatureErrorDetection;
import eu.europa.esig.dss.tsl.alerts.handlers.log.LogLOTLLocationChangeAlertHandler;
import eu.europa.esig.dss.tsl.alerts.handlers.log.LogOJUrlChangeAlertHandler;
import eu.europa.esig.dss.tsl.alerts.handlers.log.LogTLExpirationAlertHandler;
import eu.europa.esig.dss.tsl.alerts.handlers.log.LogTLSignatureErrorAlertHandler;
import eu.europa.esig.dss.tsl.cache.CacheCleaner;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.tsl.source.TLSource;
import eu.europa.esig.dss.tsl.sync.AcceptAllStrategy;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.reports.Reports;

import java.io.File;
import java.util.Arrays;

public class App {

    public static void main(String[] args) {


        String pdfPAth = "xxxxx";

        FileDocument adPDF = new FileDocument(pdfPAth);

        SignedDocumentValidator documentValidator = SignedDocumentValidator.fromDocument(adPDF);

        System.out.println(documentValidator.isSupported(adPDF));
        System.out.println(documentValidator.getSignatures());
        System.out.println(documentValidator.getSignatures().get(0).getCertificates());

//        KeyStoreCertificateSource truststore = null;
//        try {
//            truststore = new KeyStoreCertificateSource("/Users/agelos/Software/eseal/eseal.truststore.jks", "JKS", "xxxxx");
//        } catch (Exception e) {
//            System.out.println("exception");
//            System.out.println(e.getMessage());
//        }
////
//        CommonTrustedCertificateSource ctcs = new CommonTrustedCertificateSource();
//
//        ctcs.importAsTrusted(truststore);

        TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();

       CommonCertificateVerifier cv = new CommonCertificateVerifier();
//        cv.setTrustedCertSources(trustedListsCertificateSource);
//        cv.addAdjunctCertSources(ctcs);

        CommonsDataLoader commonsDataLoader = new CommonsDataLoader();
        cv.setCrlSource(new OnlineCRLSource());
        cv.setOcspSource(new OnlineOCSPSource());
        cv.setDataLoader(commonsDataLoader);

        TLValidationJob tlJob = job();
        tlJob.setDebug(true);
        tlJob.setTrustedListCertificateSource(trustedListsCertificateSource);

        tlJob.onlineRefresh();

        documentValidator.setCertificateVerifier(cv);
        documentValidator.setValidationLevel(ValidationLevel.BASIC_SIGNATURES);
        //xdocumentValidator.setValidationLevel(ValidationLevel.ARCHIVAL_DATA);
        Reports r = documentValidator.validateDocument();
        System.out.println(r.getSimpleReport().getJaxbModel().getValidationPolicy().getPolicyName());
        System.out.println(r.getDetailedReport().getBasicBuildingBlocksIndication("S-A3531E32A839C55A90281BDC437D6410C88D281F01541DB919E113FF89BF3E97"));
        System.out.println(r.getSimpleReport().getSignatureIdList());
        System.out.println(r.getSimpleReport().getIndication("S-A3531E32A839C55A90281BDC437D6410C88D281F01541DB919E113FF89BF3E97"));
        System.out.println(r.getXmlSimpleReport());
        System.out.println(cv.getAdjunctCertSources().getNumberOfEntities());
        System.out.println(r.getSimpleReport().getSignaturesCount());
    }

    public static TLValidationJob job() {
        TLValidationJob job = new TLValidationJob();
        job.setOfflineDataLoader(offlineLoader());
        job.setOnlineDataLoader(onlineLoader());
        job.setTrustedListCertificateSource(new TrustedListsCertificateSource());
        job.setSynchronizationStrategy(new AcceptAllStrategy());
        job.setCacheCleaner(cacheCleaner());

        TLSource grTL = new TLSource();
        grTL.setUrl("https://www.eett.gr/tsl/EL-TSL.xml");
        grTL.setCertificateSource(new CommonCertificateSource());
        job.setTrustedListSources(grTL);
        job.setTLAlerts(Arrays.asList(tlSigningAlert(), tlExpirationDetection()));

        return job;
    }


    private static DSSFileLoader onlineLoader() {
        FileCacheDataLoader onlineFileLoader = new FileCacheDataLoader();
        onlineFileLoader.setCacheExpirationTime(0);
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

    public static TLAlert tlSigningAlert() {
        TLSignatureErrorDetection signingDetection = new TLSignatureErrorDetection();
        LogTLSignatureErrorAlertHandler handler = new LogTLSignatureErrorAlertHandler();
        return new TLAlert(signingDetection, handler);
    }

    public static TLAlert tlExpirationDetection() {
        TLExpirationDetection expirationDetection = new TLExpirationDetection();
        LogTLExpirationAlertHandler handler = new LogTLExpirationAlertHandler();
        return new TLAlert(expirationDetection, handler);
    }

    private static CacheCleaner cacheCleaner() {
        System.out.println("cc1");
        CacheCleaner cacheCleaner = new CacheCleaner();
        cacheCleaner.setCleanFileSystem(true);
        cacheCleaner.setCleanMemory(true);
        cacheCleaner.setDSSFileLoader(offlineLoader());
        System.out.println("cc2");
        return cacheCleaner;
    }

    private static DSSFileLoader offlineLoader() {
        FileCacheDataLoader offlineFileLoader = new FileCacheDataLoader();
        offlineFileLoader.setDataLoader(new IgnoreDataLoader());
        offlineFileLoader.setCacheExpirationTime(0);
        offlineFileLoader.setFileCacheDirectory(tlCacheDirectory());
        return offlineFileLoader;
    }

}