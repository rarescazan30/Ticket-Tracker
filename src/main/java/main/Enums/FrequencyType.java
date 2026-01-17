package main.Enums;

/**
 * Enumeration for frequency levels with associated numeric weights
 */
public enum FrequencyType {
    RARE(1),
    OCCASIONAL(2),
    FREQUENT(3),
    ALWAYS(4);

    private int value;
    FrequencyType(final int value) {
        this.value = value;
    }

    /**
     * Returns the numeric value associated with the frequency level
     */
    public int getValue() {
        return value;
    }
}
