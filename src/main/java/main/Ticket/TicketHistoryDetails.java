package main.Ticket;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import main.Comments.Comment;
import main.Enums.StatusType;

import java.util.List;

/**
 * * The class is used for better formating when outputting automatically with Jackson
 * Since Jackson outputting a ticket by itself would print a lot more details,
 * TickeHistoryDetails is used to print exactly what we want from the ticket.
 * Implements the Data Transfer Object (DTO) pattern
 * */
@JsonPropertyOrder({
        "id", "title", "status", "actions", "comments"
})
public final class TicketHistoryDetails {
    private int id;
    private String title;
    private StatusType status;
    private List<Object> actions;
    private List<Comment> comments;

    /**
     * * Constructs the DTO with specific ticket details and actions
     * */
    public TicketHistoryDetails(final Ticket ticket, final List<Object> actions) {
        this.id = ticket.getId();
        this.title = ticket.getTitle();
        this.status = ticket.getStatus();
        this.actions = actions;
        this.comments = ticket.getComments();
    }

    /**
     * * Returns the ticket ID
     * */
    public int getId() {
        return id;
    }

    /**
     * * Returns the ticket title
     * */
    public String getTitle() {
        return title;
    }

    /**
     * * Returns the current status of the ticket
     * */
    public StatusType getStatus() {
        return status;
    }

    /**
     * * Returns the history of actions performed on the ticket
     * */
    public List<Object> getActions() {
        return actions;
    }

    /**
     * * Returns the list of comments associated with the ticket
     * */
    public List<Comment> getComments() {
        return comments;
    }
}
