package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Ticket.Ticket;

import java.util.List;

public class AssignTicketCommand extends BaseCommand {

    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.DEVELOPER);
    }

    public AssignTicketCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }

    @Override
    public void executeLogic() {
        AssignTicketValidator validator = new AssignTicketValidator();
        int ticketId = command.get("ticketID").asInt();
        String username = command.get("username").asText();
        validator.validate(ticketId, username);

        Ticket ticket = database.getTickets().get(ticketId);

        ticket.setAssignedTo(username);
        ticket.setAssignedAt(this.timestamp);
        ticket.setStatus(StatusType.IN_PROGRESS);
    }
}
