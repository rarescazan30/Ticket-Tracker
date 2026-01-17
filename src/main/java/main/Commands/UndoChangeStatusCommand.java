package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Exceptions.InvalidTicketAssignmentException;
import main.Ticket.Ticket;
import main.Ticket.TicketAction;
import main.Users.Developer;

import java.util.List;

/**
 * * Command responsible for reverting the status change of a ticket
 * Moves the status backwards: CLOSED -> RESOLVED -> IN_PROGRESS
 * */
public final class UndoChangeStatusCommand extends BaseCommand {

    /**
     * * Returns the roles allowed to execute this command
     * Only Developers can revert status changes on their tickets
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.DEVELOPER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public UndoChangeStatusCommand(final List<ObjectNode> outputs, final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the logic to revert ticket status
     * Updates the status to the previous state and logs the action
     * */
    @Override
    public void executeLogic() {
        String username = command.get("username").asText();
        int ticketId = command.get("ticketID").asInt();
        Developer developer = (Developer) Database.getInstance().getUser(username);

        if (database.getTickets().size() > ticketId) {
            Ticket ticket = database.getTickets().get(ticketId);
            if (!username.equals(ticket.getAssignedTo())) {
                throw new InvalidTicketAssignmentException("Ticket " + ticket.getId()
                        + " is not assigned to developer " + username + ".");
            }

            StatusType currentStatus = ticket.getStatus();
            StatusType newStatus = null;
            // logic now is CLOSED -> RESOLVED -> IN_PROGRESS
            if (currentStatus == StatusType.CLOSED) {
                newStatus = StatusType.RESOLVED;
                ticket.setSolvedAt(null);
            } else if (currentStatus == StatusType.RESOLVED) {
                newStatus = StatusType.IN_PROGRESS;
            }
            if (newStatus != null) {
                ticket.setStatus(newStatus);
                ticket.addAction(new TicketAction("STATUS_CHANGED", currentStatus.toString(),
                        newStatus.toString(), this.username, this.timestamp));
            }
        }
    }
}
