package gr.grnet.eseal;

/**
 * Enum that contains available trusted lists.
 *
 * <p>
 *     For example, {@link #GREECE} contains the greek trusted list.
 * </p>
 */
public enum TrustedListURL {

    GREECE("https://www.eett.gr/tsl/EL-TSL.xml");

    private final String name;

    private TrustedListURL(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }

}
