package main.Ticket;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

/**
 * * Represents a specific action where the system automatically unassigns a developer
 * because system removal output is different from Action output
 * */
@JsonPropertyOrder({ "from", "timestamp", "action", "by" })
public class SystemRemovalAction extends TicketAction {

    /**
     * * Constructs a system removal action log
     * */
    public SystemRemovalAction(final String oldDevUsername, final LocalDate timestamp) {
        super("REMOVED_FROM_DEV", oldDevUsername, null, "system", timestamp);
    }
}
