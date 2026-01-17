package main.Visitor;

/**
 * Data Transfer Object (DTO) used to transport performance metrics
 * Encapsulates ticket statistics for the PerformanceVisitor to process
 */
public final class PerformanceData {
    private final int closedTickets;
    private final int bugTickets;
    private final int featureTickets;
    private final int uiTickets;
    private final int highPriorityTickets;
    private final double averageResolutionTime;

    /**
     * Constructs a performance data container with all required metrics
     * @param closedTickets total number of closed tickets
     * @param bugTickets number of bug-type tickets
     * @param featureTickets number of feature-type tickets
     * @param uiTickets number of UI-type tickets
     * @param highPriorityTickets number of high priority tickets
     * @param averageResolutionTime mean time taken to resolve tickets
     */
    public PerformanceData(final int closedTickets,
                           final int bugTickets,
                           final int featureTickets,
                           final int uiTickets,
                           final int highPriorityTickets,
                           final double averageResolutionTime) {
        this.closedTickets = closedTickets;
        this.bugTickets = bugTickets;
        this.featureTickets = featureTickets;
        this.uiTickets = uiTickets;
        this.highPriorityTickets = highPriorityTickets;
        this.averageResolutionTime = averageResolutionTime;
    }

    /**
     * Returns the total number of closed tickets
     */
    public int getClosedTickets() {
        return closedTickets;
    }

    /**
     * Returns the number of bug tickets
     */
    public int getBugTickets() {
        return bugTickets;
    }

    /**
     * Returns the number of feature tickets
     */
    public int getFeatureTickets() {
        return featureTickets;
    }

    /**
     * Returns the number of UI tickets
     */
    public int getUiTickets() {
        return uiTickets;
    }

    /**
     * Returns the number of high priority tickets
     */
    public int getHighPriorityTickets() {
        return highPriorityTickets;
    }

    /**
     * Returns the average time taken to resolve tickets
     */
    public double getAverageResolutionTime() {
        return averageResolutionTime;
    }
}
