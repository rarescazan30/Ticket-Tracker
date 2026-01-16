package main.Visitor;

public class PerformanceData {
    private int closedTickets;
    private int bugTickets;
    private int featureTickets;
    private int uiTickets;
    private int highPriorityTickets;
    private double averageResolutionTime;

    public PerformanceData(int closedTickets, int bugTickets, int featureTickets, int uiTickets, int highPriorityTickets, double averageResolutionTime) {
        this.closedTickets = closedTickets;
        this.bugTickets = bugTickets;
        this.featureTickets = featureTickets;
        this.uiTickets = uiTickets;
        this.highPriorityTickets = highPriorityTickets;
        this.averageResolutionTime = averageResolutionTime;
    }
    public int getClosedTickets() { return closedTickets; }
    public int getBugTickets() { return bugTickets; }
    public int getFeatureTickets() { return featureTickets; }
    public int getUiTickets() { return uiTickets; }
    public int getHighPriorityTickets() { return highPriorityTickets; }
    public double getAverageResolutionTime() { return averageResolutionTime; }


}