package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Ticket.Ticket;
import main.Ticket.TicketAction;

import java.util.List;

/**
 * * Command responsible for assigning a ticket to a developer
 * Validates the user's eligibility and updates ticket status
 * */
public final class AssignTicketCommand extends BaseCommand {

    /**
     * * Returns the roles allowed to execute this command
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.DEVELOPER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public AssignTicketCommand(final List<ObjectNode> outputs, final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the assignment logic
     * Updates ticket assignee, status, and logs the action
     * */
    @Override
    public void executeLogic() {
        AssignTicketValidator validator = new AssignTicketValidator();
        int ticketId = command.get("ticketID").asInt();
        String username = command.get("username").asText();
        validator.validate(ticketId, username);

        if (database.getTickets().size() > ticketId) {
            Ticket ticket = database.getTickets().get(ticketId);
            ticket.setAssignedTo(username);
            ticket.setAssignedAt(this.timestamp);
            ticket.setStatus(StatusType.IN_PROGRESS);

            ticket.addAction(new TicketAction("ASSIGNED", this.username, this.timestamp));
            ticket.addAction(new TicketAction("STATUS_CHANGED", "OPEN",
                    "IN_PROGRESS", this.username, this.timestamp));
        }
    }
}
