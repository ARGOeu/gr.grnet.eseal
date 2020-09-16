package gr.grnet.eseal;

import eu.europa.esig.dss.validation.executor.ValidationLevel;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import eu.europa.esig.dss.model.DSSException;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class TestPDFValidator {

    static final String testPDFpath = "/declaration.pdf";

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @BeforeClass
    public static void setUpBeforeClass() {
        // Assert that files are present
        assertNotNull("PDF file declaration.pdf is missing", TestPDFValidator.class.getResource(testPDFpath));
    }

    @Test
    public void testPdfLoadFromPath() {
        // check that the pdf validator loads a file from a path
        PDFValidator pdfValidator = new PDFValidator(TestPDFValidator.class.getResource(testPDFpath).getFile());
        assertNotNull("Loading of the pdf document from path", pdfValidator.getPdfDocument());
    }

    @Test
    public void testPDFLoadFromFile() {
        // check that the pdf validator loads a file from a File object
        File pdfFile = new File(TestPDFValidator.class.getResource(testPDFpath).getFile());
        PDFValidator pdfValidator = new PDFValidator(pdfFile);
        assertNotNull("Loading of the pdf document from file", pdfValidator.getPdfDocument());
    }

    @Test(expected = DSSException.class)
    public void testPDFLoadInvalidPath() {
        try {
            // error when the pdf path is not valid
            PDFValidator pdfValidator = new PDFValidator("/unknown/path");
            assertNotNull("Loading of the pdf document from file", pdfValidator.getPdfDocument());
        } catch (DSSException dsse) {
            assertEquals("File not found exception", "File Not Found: /unknown/path", dsse.getMessage());
            throw dsse;
        }
    }

    @Test
    public void testDetermineLevel() {
        PDFValidator pdfValidator = new PDFValidator(TestPDFValidator.class.getResource(testPDFpath).getFile());
        assertEquals("Test BASIC_SIGNATURES mapping", ValidationLevel.BASIC_SIGNATURES, pdfValidator.determineLevel(gr.grnet.eseal.ValidationLevel.BASIC_SIGNATURES));
        assertEquals("Test TIMESTAMPS mapping",ValidationLevel.TIMESTAMPS, pdfValidator.determineLevel(gr.grnet.eseal.ValidationLevel.TIMESTAMPS));
        assertEquals("TEST LONG_TERM_DATA mapping",ValidationLevel.LONG_TERM_DATA, pdfValidator.determineLevel(gr.grnet.eseal.ValidationLevel.LONG_TERM_DATA));
        assertEquals("TEST ARCHIVAL_DATA mapping",ValidationLevel.ARCHIVAL_DATA, pdfValidator.determineLevel(gr.grnet.eseal.ValidationLevel.ARCHIVAL_DATA));
    }

    @Test
    public void testValidateWithX509CertificateTrustSourceTotalPASS() {

        PDFValidator pdfValidator = new PDFValidator(TestPDFValidator.class.getResource(testPDFpath).getFile());
        X509Certificate cert = null ;

        try {
            InputStream inStream = new FileInputStream(TestPDFValidator.class.getResource("/x509source/x509CA.cer").getFile());
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(inStream);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        X509CertificateTrustSource x509source = new X509CertificateTrustSource(cert);

        ValidationReport vr = pdfValidator.validate(gr.grnet.eseal.ValidationLevel.BASIC_SIGNATURES, x509source);
        String[] errors = new String[]{"Unable to build a certificate chain until a trusted list!"};
        assertEquals(ValidationResult.TOTAL_PASSED, vr.getValidationResult());
        Assert.assertArrayEquals("Expected detailed warnings", new String[0], vr.getWarnings());
        Assert.assertArrayEquals("Expected detailed errors", errors, vr.getErrors());
    }

    @Test
    public void testValidateWithX509CertificateTrustSourceINDETERMINATEandTIMESTAMPS() {

        PDFValidator pdfValidator = new PDFValidator(TestPDFValidator.class.getResource(testPDFpath).getFile());
        X509Certificate cert = null ;

        try {
            InputStream inStream = new FileInputStream(TestPDFValidator.class.getResource("/x509source/unknownCA.pem").getFile());
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(inStream);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        X509CertificateTrustSource x509source = new X509CertificateTrustSource(cert);

        ValidationReport vr = pdfValidator.validate(gr.grnet.eseal.ValidationLevel.TIMESTAMPS, x509source);

        String[] warnings = new String[]{
                "The signature/seal is an INDETERMINATE AdES digital signature!",
                "The signed attribute: 'signing-certificate' is present more than once!"
        };

        String[] errors = new String[]{
                "Unable to build a certificate chain until a trusted list!",
                "The result of the Basic validation process is not conclusive!",
                "The certificate chain for signature is not trusted, it does not contain a trust anchor.",
                "The result of the timestamps validation process is not conclusive!",
                "The certificate chain for timestamp is not trusted, it does not contain a trust anchor.",
        };

        assertEquals(ValidationResult.INDETERMINATE, vr.getValidationResult());
        Assert.assertArrayEquals("Expected detailed warnings", warnings, vr.getWarnings());
        Assert.assertArrayEquals("Expected detailed errors", errors, vr.getErrors());
        assertEquals(ValidationResult.INDETERMINATE, vr.getValidationResult());
    }

    @Test
    public void testValidateWithX509CertificateTrustSourceNoSign() {

        PDFValidator pdfValidator = new PDFValidator(TestPDFValidator.class.getResource("/simple-no-sign.pdf").getFile());
        X509Certificate cert = null ;

        try {
            InputStream inStream = new FileInputStream(TestPDFValidator.class.getResource("/x509source/x509CA.cer").getFile());
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(inStream);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        X509CertificateTrustSource x509source = new X509CertificateTrustSource(cert);

        ValidationReport vr = pdfValidator.validate(gr.grnet.eseal.ValidationLevel.BASIC_SIGNATURES, x509source);
        assertEquals(ValidationResult.NO_SIGNATURE, vr.getValidationResult());
        Assert.assertArrayEquals("Expected detailed warnings", new String[0], vr.getWarnings());
        Assert.assertArrayEquals("Expected detailed errors", new String[0], vr.getErrors());
    }
}
