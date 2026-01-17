package main.Enums;

/**
 * Enumeration for business priority levels with associated scores and seniority requirements
 */
public enum BusinessPriorityType {
    LOW(1, SeniorityType.JUNIOR),
    MEDIUM(2, SeniorityType.MID),
    HIGH(3, SeniorityType.MID),
    CRITICAL(4, SeniorityType.SENIOR);
    private final int score;
    private final SeniorityType requiredSeniority;
    BusinessPriorityType(final int score, final SeniorityType requiredSeniority) {
        this.score = score;
        this.requiredSeniority = requiredSeniority;
    }

    /**
     * Returns the numeric score of the priority
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the minimum seniority level required for this priority
     */
    public SeniorityType getRequiredSeniority() {
        return requiredSeniority;
    }

    /**
     * Returns the next priority level in the hierarchy
     */
    public BusinessPriorityType getNext() {
        BusinessPriorityType[] priorities = BusinessPriorityType.values();
        int nextIndex = this.ordinal() + 1;
        if (nextIndex < priorities.length) {
            return priorities[nextIndex];
        }
        return this;
    }
}
