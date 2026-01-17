package main.Users;

import main.Enums.BusinessPriorityType;
import main.Milestone.Milestone;
import main.Ticket.Ticket;
import main.Visitor.UserVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user who reports tickets but does not resolve them
 */
public final class Reporter extends User {
    /**
     * Default constructor for Reporter
     */
    public Reporter() {
        super();
    }

    /**
     * Reporter doesn't get visited for performance score
     * @param visitor the UserVisitor implementation
     */
    @Override
    public double accept(final UserVisitor visitor) {
        return 0;
    }

    /**
     * Reporters can't get overwhelmed
     */
    @Override
    public boolean checkIfOverwhelmed(final BusinessPriorityType businessPriority) {
        return false;
    }

    /**
     * * Reporters do not have access to view milestones
     * Returns an empty list
     * */
    @Override
    public List<Milestone> filterVisibleMilestones(final List<Milestone> milestones) {
        return new ArrayList<>();
    }

    /**
     * * Reporters can access tickets they've reported
     * */
    @Override
    public List<Ticket> filterVisibleTickets(final List<Ticket> allTickets,
                                             final List<Milestone> milestones) {
        List<Ticket> result = new ArrayList<>();
        for (Ticket ticket : allTickets) {
            if (ticket.getReportedBy().equals(this.getUsername())) {
                result.add(ticket);
            }
        }
        return result;
    }
}
