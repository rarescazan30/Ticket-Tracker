package main.Database;

import java.util.ArrayList;
import java.util.List;
import main.Users.User;
import main.Milestone.Milestone;
import main.Ticket.Ticket;

/**
 * Class representing a central data storage using the Singleton design pattern
 */
public final class Database {
    private static Database singleton;

    private List<User> users = new ArrayList<>();
    private List<Milestone> milestones = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();

    private Database() {
        // we want to prevent instantiations
    }

    /**
     * Returns the unique instance of the database
     */
    public static Database getInstance() {
        if (singleton == null) {
            singleton = new Database();
        }
        return singleton;
    }

    /**
     * Returns the list of all users
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Returns the list of all milestones
     */
    public List<Milestone> getMilestones() {
        return milestones;
    }

    /**
     * Returns the list of all tickets
     */
    public List<Ticket> getTickets() {
        return tickets;
    }

    /**
     * Sets the list of users
     */
    public void setUsers(final List<User> users) {
        this.users = users;
    }

    /**
     * Sets the list of milestones
     */
    public void setMilestones(final List<Milestone> milestones) {
        this.milestones = milestones;
    }

    /**
     * Sets the list of tickets
     */
    public void setTickets(final List<Ticket> tickets) {
        this.tickets = tickets;
    }

    /**
     * Retrieves a user by their username
     */
    public User getUser(final String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Generates a new unique ticket ID based on the current list size
     */
    public int getNewTicketId() {
        return this.tickets.size();
    }

    /**
     * Adds a new ticket to the database
     */
    public void addTicket(final Ticket ticket) {
        this.tickets.add(ticket);
    }

    /**
     * Adds a new milestone to the database
     */
    public void addMilestone(final Milestone milestone) {
        this.milestones.add(milestone);
    }

    /**
     * Clears all stored data and resets the singleton instance
     */
    public static void reset() {
        if (singleton != null) {
            singleton.users.clear();
            singleton.milestones.clear();
            singleton.tickets.clear();
            singleton = null;
        }
    }
}
