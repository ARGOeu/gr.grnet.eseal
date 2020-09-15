package gr.grnet.eseal;

/**
 * <p>
 *     Validation level dictates the severity of the validation process.
 * </p>
 */
public enum ValidationLevel {
    BASIC_SIGNATURES,
    TIMESTAMPS,
    LONG_TERM_DATA,
    ARCHIVAL_DATA;

    private ValidationLevel() {
    }
}