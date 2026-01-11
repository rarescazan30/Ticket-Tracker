package main.Enums;

public enum SeverityType {
    MINOR(1),
    MODERATE(2),
    SEVERE(3);

    private int value;
    SeverityType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
