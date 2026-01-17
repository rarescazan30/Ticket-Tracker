package main.Visitor;

import main.Users.Developer;

/**
 * Implementation of the Visitor pattern that calculates performance scores for users
 * Uses PerformanceData as a Data Transfer Object (DTO) to encapsulate metrics
 * without exposing complex logic or database dependencies during the visit
 */
public final class PerformanceVisitor implements UserVisitor {
    private static final double CLOSED_TICKET_WEIGHT = 0.5;
    private static final double MID_PRIORITY_WEIGHT = 0.7;
    private static final double MID_RESOLUTION_WEIGHT = 0.3;
    private static final double MID_BASE_BONUS = 15.0;
    private static final double SENIOR_PRIORITY_WEIGHT = 1.0;
    private static final double SENIOR_RESOLUTION_WEIGHT = 0.5;
    private static final double SENIOR_BASE_BONUS = 30.0;
    private static final double JUNIOR_BASE_BONUS = 5.0;
    private static final double DIVERSITY_DIVISOR = 3.0;

    private final PerformanceData performanceData;

    /**
     * Constructs a visitor with the required metrics encapsulated in a DTO
     * DTO manages all fields whilst keeping my code clean in PerformanceVisitor
     */
    public PerformanceVisitor(final PerformanceData performanceData) {
        this.performanceData = performanceData;
    }

    @Override
    public double visit(final Developer dev) {
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

    /**
     * Calculates the performance score for a Junior Developer
     */
    private double calculateJuniorScore() {
        double diversity = ticketDiversityFactor(
                performanceData.getBugTickets(),
                performanceData.getFeatureTickets(),
                performanceData.getUiTickets()
        );
        double base = (CLOSED_TICKET_WEIGHT * performanceData.getClosedTickets()) - diversity;
        return Math.max(0, base) + JUNIOR_BASE_BONUS;
    }

    /**
     * Calculates the performance score for a Mid Developer
     */
    private double calculateMidScore() {
        double base = (CLOSED_TICKET_WEIGHT * performanceData.getClosedTickets())
                + (MID_PRIORITY_WEIGHT * performanceData.getHighPriorityTickets())
                - (MID_RESOLUTION_WEIGHT * performanceData.getAverageResolutionTime());
        return Math.max(0, base) + MID_BASE_BONUS;
    }

    /**
     * Calculates the performance score for a Senior Developer
     */
    private double calculateSeniorScore() {
        double base = (CLOSED_TICKET_WEIGHT * performanceData.getClosedTickets())
                + (SENIOR_PRIORITY_WEIGHT * performanceData.getHighPriorityTickets())
                - (SENIOR_RESOLUTION_WEIGHT * performanceData.getAverageResolutionTime());
        return Math.max(0, base) + SENIOR_BASE_BONUS;
    }

    /**
     * Computes a factor based on the distribution of ticket types
     * @param bug number of bug tickets
     * @param feature number of feature tickets
     * @param ui number of ui tickets
     */
    private double ticketDiversityFactor(final int bug, final int feature, final int ui) {
        double mean = (bug + feature + ui) / DIVERSITY_DIVISOR;
        if (mean == 0.0) {
            return 0.0;
        }
        double varBug = Math.pow(bug - mean, 2);
        double varFeat = Math.pow(feature - mean, 2);
        double varUi = Math.pow(ui - mean, 2);
        double variance = (varBug + varFeat + varUi) / DIVERSITY_DIVISOR;
        double std = Math.sqrt(variance);

        return (std / mean);
    }

    /**
     * Returns the performance metrics DTO
     */
    public PerformanceData getPerformanceData() {
        return performanceData;
    }
}
