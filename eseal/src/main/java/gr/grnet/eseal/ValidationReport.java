package gr.grnet.eseal;

import eu.europa.esig.dss.validation.reports.Reports;

/**
 * <p>
 *     ValidationReport holds information regarding the result of the validation process.
 * </p>
 *
 */
public class ValidationReport {

    private Reports ddsReport;
    private ValidationResult validationResult ;

    /** Creates a validation report based on the result of a dss validator.
     * @param ddsReport The dss report after the validation process.
     */
    public ValidationReport(Reports ddsReport) {
        this.ddsReport = ddsReport;
    }
}
