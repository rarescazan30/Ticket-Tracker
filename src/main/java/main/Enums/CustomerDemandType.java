package main.Enums;

/**
 * Enumeration for customer demand levels with associated priority values
 */
public enum CustomerDemandType {
    LOW(1),
    MEDIUM(3),
    HIGH(6),
    VERY_HIGH(10);

    private int value;
    CustomerDemandType(final int value) {
        this.value = value;
    }

    /**
     * Returns the integer value associated with the demand type
     */
    public int getValue() {
        return value;
    }
}
