package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Ticket.Ticket;
import main.Ticket.TicketAction;

import java.util.List;

public class UndoAssignTicketCommand extends BaseCommand {
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.DEVELOPER);
    }
    public UndoAssignTicketCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }
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
