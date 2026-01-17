package main.Ticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

/**
 * * Represents an action or event recorded in a ticket's history
 * */
// we tell jackson how to format the output
// depending on case we'll ignore null entries
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "from", "to", "milestone", "by", "timestamp", "action"
})
public class TicketAction {
    private String action;
    private String from;
    private String to;
    private String milestone;
    private String by;
    private String timestamp;

    /**
     * * Creates a basic action record
     * */
    public TicketAction(final String action, final String by, final LocalDate timestamp) {
        this.action = action;
        this.by = by;
        this.timestamp = timestamp.toString();
    }

    /**
     * * Creates an action record involving a state transition (from -> to)
     * */
    public TicketAction(final String action, final String from,
                        final String to, final String by, final LocalDate timestamp) {
        this.action = action;
        this.from = from;
        this.to = to;
        this.by = by;
        this.timestamp = timestamp.toString();
    }

    /**
     * * Creates an action record related to a milestone
     * */
    public TicketAction(final String action, final String milestone, final String by,
                        final LocalDate timestamp, final boolean isMilestone) {
        this.action = action;
        this.milestone = milestone;
        this.by = by;
        this.timestamp = timestamp.toString();
    }

    /**
     * * Returns the description of the action
     * */
    public final String getAction() {
        return action;
    }

    /**
     * * Returns the initial state or value
     * */
    public final String getFrom() {
        return from;
    }

    /**
     * * Returns the final state or value
     * */
    public final String getTo() {
        return to;
    }

    /**
     * * Returns the associated milestone name, if any
     * */
    public final String getMilestone() {
        return milestone;
    }

    /**
     * * Returns the username of the person who performed the action
     * */
    public final String getBy() {
        return by;
    }

    /**
     * * Returns the timestamp of the action as a string
     * */
    public final String getTimestamp() {
        return timestamp;
    }
}
