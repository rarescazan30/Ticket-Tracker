package main.Commands;

import main.Database.Database;
import main.Enums.StatusType;
import main.Exceptions.InvalidStatusException;
import main.Ticket.Ticket;

public class UndoAssignValidator {
    public void validate(int ticketId) {
        Database database = Database.getInstance();
        Ticket ticket =  database.getTickets().get(ticketId);
        if (ticket.getStatus() != StatusType.IN_PROGRESS) {
            throw new InvalidStatusException("Only IN_PROGRESS tickets can be unassigned.");
        }
    }
}
