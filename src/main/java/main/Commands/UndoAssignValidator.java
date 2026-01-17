package main.Commands;

import main.Database.Database;
import main.Enums.StatusType;
import main.Exceptions.InvalidStatusException;
import main.Ticket.Ticket;

/**
 * * Validator responsible for checking if an assignment can be undone
 * Ensures that only tickets currently in progress can be unassigned
 * */
public final class UndoAssignValidator {

    /**
     * * Validates that the ticket status allows unassignment
     * */
    public void validate(final int ticketId) {
        Database database = Database.getInstance();
        Ticket ticket = database.getTickets().get(ticketId);
        if (ticket.getStatus() != StatusType.IN_PROGRESS) {
            throw new InvalidStatusException("Only IN_PROGRESS tickets can be unassigned.");
        }
    }
}
