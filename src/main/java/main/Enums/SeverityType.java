package main.Enums;

/**
 * Enumeration for ticket severity levels with associated numeric weights
 */
public enum SeverityType {
    MINOR(1),
    MODERATE(2),
    SEVERE(3);

    private int value;
    SeverityType(final int value) {
        this.value = value;
    }

    /**
     * Returns the numeric value associated with the severity level
     */
    public int getValue() {
        return value;
    }
}
