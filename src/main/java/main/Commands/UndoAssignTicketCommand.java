package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Ticket.Ticket;
import main.Ticket.TicketAction;

import java.util.List;

/**
 * * Command responsible for reverting a ticket assignment
 * Allows a developer to unassign themselves from a ticket
 * */
public final class UndoAssignTicketCommand extends BaseCommand {

    /**
     * * Returns the list of roles allowed to execute this command
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.DEVELOPER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public UndoAssignTicketCommand(final List<ObjectNode> outputs, final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the unassignment logic
     * Validates the request, updates ticket status, and logs the action
     * */
    @Override
    public void executeLogic() {
        int ticketId = command.get("ticketID").asInt();
        UndoAssignValidator validator = new UndoAssignValidator();
        validator.validate(ticketId);
        Ticket ticket = Database.getInstance().getTickets().get(ticketId);
        ticket.setAssignedTo(null);
        ticket.setStatus(StatusType.OPEN);
        ticket.setAssignedAt(null);
        ticket.addAction(new TicketAction("DE-ASSIGNED", this.username, this.timestamp));
    }
}
