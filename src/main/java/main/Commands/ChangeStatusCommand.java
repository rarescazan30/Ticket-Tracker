package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Exceptions.InvalidTicketAssignmentException;
import main.Milestone.Milestone;
import main.Notifications.NotificationService;
import main.Ticket.Ticket;
import main.Ticket.TicketAction;
import main.Users.Developer;

import java.time.LocalDate;
import java.util.List;

public class ChangeStatusCommand extends BaseCommand {
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.DEVELOPER);
    }
    public ChangeStatusCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }

    @Override
    public void executeLogic() {
        String username = command.get("username").asText();
        int ticketId = command.get("ticketID").asInt();
        Developer developer = (Developer)(Database.getInstance().getUser(username));
        if (database.getTickets().size() > ticketId) {
            Ticket ticket = database.getTickets().get(ticketId);
            if (!username.equals(ticket.getAssignedTo())) {
                throw new InvalidTicketAssignmentException("Ticket " + ticket.getId() + " is not assigned to developer " + username + ".");
            }

            StatusType currentStatus = ticket.getStatus();
            StatusType newStatus = null;

            // IN_PROGRESS -> RESOLVED -> CLOSED
            if (currentStatus == StatusType.IN_PROGRESS) {
                newStatus = StatusType.RESOLVED;
                ticket.setSolvedAt(this.timestamp);
            } else if (currentStatus == StatusType.RESOLVED) {
                newStatus = StatusType.CLOSED;

            }
            if (newStatus != null) {
                ticket.setStatus(newStatus);
                ticket.addAction(new TicketAction("STATUS_CHANGED", currentStatus.toString(), newStatus.toString(), this.username, this.timestamp));
                if (newStatus == StatusType.CLOSED) {
                    for (Milestone m : Database.getInstance().getMilestones()) {
                        if (m.getTickets().contains(ticket.getId())) {
                            m.resolvePostCompletionActions(ticket, this.timestamp.toString());
                            break;
                        }
                    }
                }
            }
        }
    }
}
