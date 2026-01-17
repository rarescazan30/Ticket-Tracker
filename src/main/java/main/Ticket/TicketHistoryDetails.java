package main.Ticket;

/*
* we use this for better formating when outputing automatically with json
* since Json outputting a ticket by itself would print a lot more details
* we make this class that prints exactly what we want from the ticket
* */

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import main.Comments.Comment;
import main.Enums.StatusType;

import java.util.List;

@JsonPropertyOrder( {
        "id", "title", "status", "actions", "comments"
})
public class TicketHistoryDetails {
    private int  id;
    private String title;
    private StatusType status;
    private List<Object> actions;
    private List<Comment> comments;

    public TicketHistoryDetails(Ticket ticket, List<Object> actions) {
        this.id = ticket.getId();
        this.title = ticket.getTitle();
        this.status = ticket.getStatus();
        this.actions = actions;
        this.comments = ticket.getComments();
    }
    public int getId() { return id; }
    public String getTitle() { return title; }
    public StatusType getStatus() { return status; }
    public List<Object> getActions() { return actions; }
    public List<Comment> getComments() { return comments; }
}
