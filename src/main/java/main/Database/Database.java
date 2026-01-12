package main.Database;

import java.util.ArrayList;
import java.util.List;
import main.Users.User;
import main.Milestone.Milestone;
import main.Ticket.Ticket;

/*
* TODO: Explain usage of Singleton
*
* */

public class Database {
    private static Database singleton;

    private List<User> users = new ArrayList<>();
    private List<Milestone> milestones = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();
    private Database() {
        // we want to prevent instantiations
    }
    public static Database getInstance() {
        if (singleton == null) {
            singleton = new Database();
        }
        return singleton;
    }
    public List<User> getUsers() {
        return users;
    }
    public List<Milestone> getMilestones() {
        return milestones;
    }
    public List<Ticket> getTickets() {
        return tickets;
    }
    public void  setUsers(List<User> users) {
        this.users = users;
    }
    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }
    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public int getNewTicketId() {
        return this.tickets.size();
    }
    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
    }

}
