package gr.grnet.eseal;

import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.pades.validation.PDFDocumentValidator;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.reports.Reports;
import java.io.File;

/**
 * <p>
 *      PDFValidator is the main building block of the validation process.
 * </p>
 */
public class PDFValidator {

    private FileDocument pdfDocument;

    /** Creates a pdf validator from the given pdf(path to pdf).
     * @param filepath path to the file.
     */
    public PDFValidator(String filepath) {
        this.pdfDocument = new  FileDocument(filepath);
    }

    /** Creates a pdf validator from the given pdf(File object).
     * @param file java.io.File object representing the pdf document.
     */
    public PDFValidator(File file) {
        this.pdfDocument = new FileDocument(file);
        }

    /** Gets the pdf document.
     * @return A FileDocument representing the pdf of the validation
     */
    public FileDocument getPdfDocument() {
        return this.pdfDocument;
    }

    /**
     * Performs the validation process with the given trust source
     * @param validationLevel the level of validation severity
     * @param x509CertificateTrustSource the trust source that will be used to validate the document
     * @return ValidationReport that contains information regarding the validation process
     */
    public ValidationReport validate(ValidationLevel validationLevel, X509CertificateTrustSource x509CertificateTrustSource) {

        // build the certificate verifier for the pdf validator
        CertificateVerifier cv =  new CommonCertificateVerifier();
        CommonsDataLoader commonsDataLoader = new CommonsDataLoader();
        cv.setCrlSource(new OnlineCRLSource());
        cv.setOcspSource(new OnlineOCSPSource());
        cv.setDataLoader(commonsDataLoader);
        cv.setTrustedCertSources(x509CertificateTrustSource.getCommonTrustedCertificateSource());

        // initialize the dss validator
        PDFDocumentValidator dssValidator = new PDFDocumentValidator(this.pdfDocument);
        dssValidator.setValidationLevel(determineLevel(validationLevel));
        dssValidator.setCertificateVerifier(cv);

        Reports r = dssValidator.validateDocument();

        return new ValidationReport(r);
    }

    /**
     * Performs the validation process with the given trust source
     * @param validationLevel the level of validation severity
     * @param tlTrustSource the trust source that will be used to validate the document
     * @return ValidationReport that contains information regarding the validation process
     */
    public ValidationReport validate(ValidationLevel validationLevel, TLTrustSource tlTrustSource) {

        TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();

        // build the certificate verifier for the pdf validator
        CertificateVerifier cv =  new CommonCertificateVerifier();
        CommonsDataLoader commonsDataLoader = new CommonsDataLoader();
        cv.setCrlSource(new OnlineCRLSource());
        cv.setOcspSource(new OnlineOCSPSource());
        cv.setDataLoader(commonsDataLoader);
        cv.setTrustedCertSources(trustedListsCertificateSource);

        tlTrustSource.getJob().setTrustedListCertificateSource(trustedListsCertificateSource);
        tlTrustSource.getJob().onlineRefresh();

        // initialize the dss validator
        PDFDocumentValidator dssValidator = new PDFDocumentValidator(this.pdfDocument);
        dssValidator.setValidationLevel(determineLevel(validationLevel));
        dssValidator.setCertificateVerifier(cv);

        Reports r = dssValidator.validateDocument();

        return new ValidationReport(r);
    }

    /**
     * Performs the validation process with the given trust source
     * @param validationLevel the level of validation severity
     * @param keystoreTrustSource the trust source that will be used to validate the document
     * @return ValidationReport that contains information regarding the validation process
     */
    public ValidationReport validate(ValidationLevel validationLevel, KeystoreTrustSource keystoreTrustSource) {

        // build the certificate verifier for the pdf validator
        CertificateVerifier cv =  new CommonCertificateVerifier();
        CommonsDataLoader commonsDataLoader = new CommonsDataLoader();
        cv.setCrlSource(new OnlineCRLSource());
        cv.setOcspSource(new OnlineOCSPSource());
        cv.setDataLoader(commonsDataLoader);
        cv.setTrustedCertSources(keystoreTrustSource.getCommonTrustedCertificateSource());

        // initialize the dss validator
        PDFDocumentValidator dssValidator = new PDFDocumentValidator(this.pdfDocument);
        dssValidator.setValidationLevel(determineLevel(validationLevel));
        dssValidator.setCertificateVerifier(cv);

        Reports r = dssValidator.validateDocument();

        return new ValidationReport(r);
    }

    /**
     * Maps the library's validation level to the proper dss one
     * @param validationLevel validation level to be mapped
     * @return eu.europa.esig.dss.validation.executor.ValidationLevel dss validation level
     */
    public eu.europa.esig.dss.validation.executor.ValidationLevel determineLevel(ValidationLevel validationLevel) {

        eu.europa.esig.dss.validation.executor.ValidationLevel vl  = eu.europa.esig.dss.validation.executor.ValidationLevel.BASIC_SIGNATURES;

        switch ( validationLevel) {
            case BASIC_SIGNATURES:
               return vl;
            case TIMESTAMPS:
                vl = eu.europa.esig.dss.validation.executor.ValidationLevel.TIMESTAMPS;
                return vl;
            case LONG_TERM_DATA:
                vl = eu.europa.esig.dss.validation.executor.ValidationLevel.LONG_TERM_DATA;
                return vl;
            case ARCHIVAL_DATA:
                vl = eu.europa.esig.dss.validation.executor.ValidationLevel.ARCHIVAL_DATA;
                return vl;
        }

        return vl;
    }

}
