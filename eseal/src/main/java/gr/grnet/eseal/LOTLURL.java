package gr.grnet.eseal;

/**
 * Enum that contains available lists of trusted lists.
 *
 * <p>
 *     For example, {@link #EUROPE} contains the european list of trusted lists.
 * </p>
 */
public enum LOTLURL {

    EUROPE("https://ec.europa.eu/tools/lotl/eu-lotl.xml");

    private final String name;

    private LOTLURL(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }

}
