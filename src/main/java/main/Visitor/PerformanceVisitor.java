package main.Visitor;

import main.Users.Developer;
import main.Users.Manager;
import main.Users.User;
import main.Visitor.PerformanceData;
import main.Visitor.UserVisitor;

public class PerformanceVisitor implements UserVisitor {
    public final PerformanceData performanceData;
    public PerformanceVisitor(PerformanceData performanceData) {
        this.performanceData = performanceData; // we use the intermediary class to make code cleaner
    }

    @Override
    public double visit(Developer dev) {
        if (performanceData.getClosedTickets() == 0) {
            return 0.0;
        }

        switch (dev.getSeniority()) {
            case JUNIOR:
                return calculateJuniorScore();
            case MID:
                return calculateMidScore();
            case SENIOR:
                return calculateSeniorScore();
            default:
                return 0.0;
        }
    }

    private double calculateJuniorScore() {
        double diversity = ticketDiversityFactor(performanceData.getBugTickets(), performanceData.getFeatureTickets(), performanceData.getUiTickets());
        double base = (0.5 * performanceData.getClosedTickets()) - diversity;
        return Math.max(0, base) + 5.0;
    }

    private double calculateMidScore() {
        double base = (0.5 * performanceData.getClosedTickets())
                + (0.7 * performanceData.getHighPriorityTickets())
                - (0.3 * performanceData.getAverageResolutionTime());
        return Math.max(0, base) + 15.0;
    }

    private double calculateSeniorScore() {
        double base = (0.5 * performanceData.getClosedTickets())
                + (1.0 * performanceData.getHighPriorityTickets())
                - (0.5 * performanceData.getAverageResolutionTime());
        return Math.max(0, base) + 30.0;
    }

    private double ticketDiversityFactor(int bug, int feature, int ui) {
        double mean = (bug + feature + ui) / 3.0;
        if (mean == 0.0) {
            return 0.0;
        }
        double variance = (Math.pow(bug - mean, 2) + Math.pow(feature - mean, 2) + Math.pow(ui - mean, 2)) / 3.0;
        double std = Math.sqrt(variance);

        return (std / mean);
    }
}