package main.Users;

import main.Enums.BusinessPriorityType;
import main.Enums.ExpertiseAreaType;
import main.Enums.SeniorityType;
import main.Enums.StatusType;
import main.Milestone.Milestone;
import main.Ticket.Ticket;
import main.Visitor.UserVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a developer user who can resolve tickets and has performance metrics
 */
public final class Developer extends User {
    private String hireDate;
    private ExpertiseAreaType expertiseArea;
    private SeniorityType seniority;
    private List<String> notifications = new ArrayList<>();
    private double performanceScore;

    /**
     * Default constructor for Developer
     */
    public Developer() {
        super();
    }

    /**
     * Returns the date the developer was hired
     */
    public String getHireDate() {
        return hireDate;
    }

    /**
     * Returns the expertise area of the developer
     */
    public ExpertiseAreaType getExpertiseArea() {
        return expertiseArea;
    }

    /**
     * Returns the seniority level of the developer
     */
    public SeniorityType getSeniority() {
        return seniority;
    }

    /**
     * Returns the current performance score
     */
    public double getPerformanceScore() {
        return this.performanceScore;
    }

    /**
     * Adds a notification message to the developer's list
     */
    public void addNotification(final String notification) {
        this.notifications.add(notification);
    }

    /**
     * Returns a copy of current notifications and clears the internal list
     */
    public List<String> getNotificationsAndClear() {
        List<String> currentNotifications = new ArrayList<>(this.notifications);
        this.notifications.clear(); // we delete once we use it (we use = we print it)
        return currentNotifications;
    }

    /**
     * Acceptance method for the UserVisitor
     */
    @Override
    public double accept(final UserVisitor visitor) {
        return visitor.visit(this);
    }

    /**
     * Determines if the developer is overwhelmed based on priority and seniority
     */
    @Override
    public boolean checkIfOverwhelmed(final BusinessPriorityType businessPriority) {
        boolean overwhelmed = false;
        if (businessPriority == BusinessPriorityType.CRITICAL
                || businessPriority == BusinessPriorityType.HIGH) {
            if (getSeniority() != SeniorityType.SENIOR) {
                overwhelmed = true;
            }
        }
        return overwhelmed;
    }

    /**
     * Updates the performance score for the developer
     */
    public void setPerformanceScore(final double score) {
        this.performanceScore = score;
    }

    /**
     * * Filters milestones to show only those assigned to this developer
     * */
    @Override
    public List<Milestone> filterVisibleMilestones(final List<Milestone> milestones) {
        List<Milestone> filtered = new ArrayList<>();
        for (Milestone m : milestones) {
            if (m.getAssignedDevs().contains(this.getUsername())) {
                filtered.add(m);
            }
        }
        return filtered;
    }
    /**
     * * Developers can see open tickets that aren't blocked
     * */
    @Override
    public List<Ticket> filterVisibleTickets(final List<Ticket> allTickets,
                                             final List<Milestone> milestones) {
        Set<Integer> blockedTicketsIds = new HashSet<>();
        for (Milestone milestone : milestones) {
            if (milestone.getIsBlocked()) {
                blockedTicketsIds.addAll(milestone.getTickets());
            }
        }

        List<Ticket> result = new ArrayList<>();
        for (Ticket ticket : allTickets) {
            if (ticket.getStatus() == StatusType.OPEN
                && !blockedTicketsIds.contains(ticket.getId())) {
                result.add(ticket);
            }
        }
        return result;
    }
}
