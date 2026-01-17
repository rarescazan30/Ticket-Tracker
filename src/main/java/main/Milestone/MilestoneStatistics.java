package main.Milestone;

import main.Database.Database;
import main.Ticket.Ticket;
import main.Ticket.TicketAction;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for calculating metrics and statistics related to milestones
 */
public final class MilestoneStatistics {
    private static final double PERCENT_FACTOR = 100.0;

    private MilestoneStatistics() {
        // utility class
    }

    /**
     * Calculates the completion percentage based on closed tickets in the milestone
     */
    public static double getCompletionPercentage(final Milestone milestone) {
        if (milestone.getTickets().isEmpty()) {
            return 0.00;
        }
        int nrOfClosed = milestone.getClosedTickets().size();
        double percentage = (double) nrOfClosed / (double) milestone.getTickets().size();
        return (Math.round(percentage * PERCENT_FACTOR) / PERCENT_FACTOR);
    }

    /**
     * Finds the date when the last ticket of the milestone was closed
     */
    public static LocalDate getCompletedDate(final Milestone milestone) {
        LocalDate lastCompletedDate = null;

        for (int ticketId : milestone.getTickets()) {
            Ticket ticket = Database.getInstance().getTickets().get(ticketId);
            if (ticket.getActions() != null) {
                for (TicketAction action : ticket.getActions()) {
                    if (action.getAction().equals("STATUS_CHANGED")
                        && action.getTo().equals("CLOSED")) {
                        LocalDate actionDate = LocalDate.parse(action.getTimestamp());
                        if (lastCompletedDate == null || actionDate.isAfter(lastCompletedDate)) {
                            lastCompletedDate = actionDate;
                        }
                    }
                }
            }
        }
        return lastCompletedDate;
    }

    /**
     * Calculates days until the milestone is due relative to the requested date
     */
    public static int getDaysUntilDue(final Milestone milestone, final LocalDate requestedDate) {
        LocalDate dueDate = LocalDate.parse(milestone.getDueDate());
        if (milestone.getStatus().equals("COMPLETED")) {
            LocalDate completedDate = getCompletedDate(milestone);
            if (completedDate != null) {
                int result = ((int) ChronoUnit.DAYS.between(completedDate, dueDate) + 1);
                return Math.max(result, 0);
            }
        }
        int result = ((int) ChronoUnit.DAYS.between(requestedDate, dueDate) + 1);
        return Math.max(result, 0);
    }

    /**
     * Calculates how many days a milestone is overdue
     */
    public static int getOverdueBy(final Milestone milestone, final LocalDate requestedDate) {
        LocalDate dueDate = LocalDate.parse(milestone.getDueDate());
        LocalDate endDate = requestedDate;
        if (milestone.getStatus().equals("COMPLETED")) {
            LocalDate maxClosedDate = getCompletedDate(milestone);
            if (maxClosedDate != null) {
                endDate = maxClosedDate;
            }
        }
        int result = (int) ChronoUnit.DAYS.between(dueDate, endDate) + 1;
        return Math.max(result, 0);
    }
}
