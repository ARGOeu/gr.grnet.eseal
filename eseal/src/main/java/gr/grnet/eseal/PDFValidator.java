package gr.grnet.eseal;

import eu.europa.esig.dss.model.FileDocument;
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
}
