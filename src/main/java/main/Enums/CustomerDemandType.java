package main.Enums;

public enum CustomerDemandType {
    LOW(1),
    MEDIUM(3),
    HIGH(6),
    VERY_HIGH(10);

    private int value;
    CustomerDemandType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
