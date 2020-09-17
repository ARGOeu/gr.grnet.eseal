package gr.grnet.eseal;

/**
 * <p>
 *      Validation Results that can be used to determine the outcome of a pdf validation process.
 * </p>
 *
 * <p>
 *      Generally and following ETSI standard, the validation process of an electronic signature
 *      must provide one of these three following statuses: TOTAL-FAILED, TOTAL-PASSED or INDETERMINATE.
 * </p>
 *
 * <p>
 *     A {@link #TOTAL_PASSED} response indicates that the signature has passed verification and it complies with the signature validation policy.
 * </p>
 *
 * <p>
 *     A {@link #TOTAL_FAIL} response indicates that either the signature format is incorrect or that the digital signature value fails the verification.
 * </p>
 *
 * <p>
 *      An {@link #INDETERMINATE} validation response indicates that the format and digital signature verifications have not failed
 *      but there is an insufficient information to determine if the electronic signature is valid.
 * </p>
 *
 *  * <p>
 *      An {@link #NO_SIGNATURE} validation response indicates that the document contained no signatures
 * </p>
 */
public enum ValidationResult {
    TOTAL_PASSED,
    TOTAL_FAIL,
    INDETERMINATE,
    NO_SIGNATURE;

    private ValidationResult() {
    }
}