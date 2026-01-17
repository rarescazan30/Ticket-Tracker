package main.Enums;

/**
 * Enumeration for business value sizes with associated numeric weights
 */
public enum BusinessValueType {
    S(1),
    M(3),
    L(6),
    XL(10);

    private int value;
    BusinessValueType(final int value) {
        this.value = value;
    }

    /**
     * Returns the numeric value associated with the business value size
     */
    public int getValue() {
        return value;
    }
}
