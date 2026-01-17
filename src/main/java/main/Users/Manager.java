package main.Users;

import main.Enums.BusinessPriorityType;
import main.Milestone.Milestone;
import main.Ticket.Ticket;
import main.Visitor.UserVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user with managerial responsibilities
 */
public final class Manager extends User {
    private String hireDate;
    private List<String> subordinates;

    /**
     * Default constructor for Jackson serialization
     */
    public Manager() {
        super();
        // now Jackson handles this part automatically
        this.subordinates = new ArrayList<>();
    }

    /**
     * managers dont need performance reports
     * @param visitor the UserVisitor implementation
     */
    @Override
    public double accept(final UserVisitor visitor) {
        return 0;
    }

    /**
     * Returns the date the manager was hired
     */
    public String getHireDate() {
        return hireDate;
    }

    /**
     * Returns the list of subordinate usernames
     */
    public List<String> getSubordinates() {
        return subordinates;
    }

    /**
     * Checks if the manager is overwhelmed by the current workload
     */
    @Override
    public boolean checkIfOverwhelmed(final BusinessPriorityType businessPriority) {
        return false;
    }
    /**
     * * Filters milestones to show only those created by this manager
     * */
    @Override
    public List<Milestone> filterVisibleMilestones(final List<Milestone> milestones) {
        List<Milestone> filtered = new ArrayList<>();
        for (Milestone m : milestones) {
            if (m.getCreatedBy().equals(this.getUsername())) {
                filtered.add(m);
            }
        }
        return filtered;
    }
    /**
     * * Shows all tickets for the manager to see
     * */
    @Override
    public List<Ticket> filterVisibleTickets(final List<Ticket> allTickets,
                                             final List<Milestone> milestones) {
        return new ArrayList<>(allTickets);
    }
}
