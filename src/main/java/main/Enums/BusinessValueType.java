package main.Enums;

public enum BusinessValueType {
    S(1),
    M(3),
    L(6),
    XL(10);

    private int value;
    BusinessValueType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
