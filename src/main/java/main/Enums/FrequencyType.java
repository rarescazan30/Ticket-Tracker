package main.Enums;

public enum FrequencyType {
    RARE(1),
    OCCASIONAL(2),
    FREQUENT(3),
    ALWAYS(4);

    private int value;
    FrequencyType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
