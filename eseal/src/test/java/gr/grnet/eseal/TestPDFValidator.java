package gr.grnet.eseal;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import java.io.File;
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

}
