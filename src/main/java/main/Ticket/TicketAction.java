package main.Ticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

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

    public TicketAction(String action, String by, LocalDate timestamp) {
        this.action = action;
        this.by = by;
        this.timestamp = timestamp.toString();
    }

    public TicketAction(String action, String from, String to, String by, LocalDate timestamp) {
        this.action = action;
        this.from = from;
        this.to = to;
        this.by = by;
        this.timestamp = timestamp.toString();
    }

    public TicketAction(String action, String milestone, String by, LocalDate timestamp, boolean isMilestone) {
        this.action = action;
        this.milestone = milestone;
        this.by = by;
        this.timestamp = timestamp.toString();
    }
    public String getAction() { return action; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getMilestone() { return milestone; }
    public String getBy() { return by; }
    public String getTimestamp() { return timestamp; }
}
