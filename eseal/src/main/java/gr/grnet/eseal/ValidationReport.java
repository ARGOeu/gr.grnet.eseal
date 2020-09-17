package gr.grnet.eseal;

import eu.europa.esig.dss.validation.reports.Reports;

/**
 * <p>
 *     ValidationReport holds information regarding the result of the validation process.
 * </p>
 *
 */
public class ValidationReport {

    private Reports rawDDSReport;
    private ValidationResult validationResult ;
    private String xmlSimpleReport;
    private String xmlDetailedReport;
    private String[] errors ;
    private String[] warnings ;


    /** Creates a validation report based on the result of a dss validator.
     * @param ddsReport The dss report after the validation process.
     */
    public ValidationReport(Reports ddsReport) {
        this.rawDDSReport = ddsReport;
        this.errors = new String[0];
        this.warnings = new String[0];
        this.xmlDetailedReport = ddsReport.getXmlDetailedReport();
        this.xmlSimpleReport = ddsReport.getXmlSimpleReport();

        String signatureId = ddsReport.getDetailedReport().getFirstSignatureId();
        if (signatureId == null) {
            this.validationResult = ValidationResult.NO_SIGNATURE;
        } else {
            String indication = ddsReport.getSimpleReport().getIndication(signatureId).name();

            this.errors = new String[ddsReport.getDetailedReport().getErrors(signatureId).size()];
            ddsReport.getDetailedReport().getErrors(signatureId).toArray(this.errors);

            this.warnings = new String[ddsReport.getDetailedReport().getWarnings(signatureId).size()];
            ddsReport.getDetailedReport().getWarnings(signatureId).toArray(this.warnings);

            switch (indication) {
                case "TOTAL_FAIL":
                    this.validationResult = ValidationResult.TOTAL_FAIL;
                    break;
                case "INDETERMINATE":
                    this.validationResult = ValidationResult.INDETERMINATE;
                    break;
                case "TOTAL_PASSED":
                    this.validationResult = ValidationResult.TOTAL_PASSED;
                    break;
            }
        }
    }

    public Reports getRawDDSReport() {
        return rawDDSReport;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }


    public String getXmlSimpleReport() {
        return xmlSimpleReport;
    }


    public String getXmlDetailedReport() {
        return xmlDetailedReport;
    }

    public String[] getErrors() {
        return errors;
    }

    public String[] getWarnings() {
        return warnings;
    }
}
