package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Enums.RoleType;
import main.Exceptions.InvalidPeriodException;
import main.Exceptions.TicketAlreadyAssignedException;
import main.Milestone.Milestone;
import main.PeriodLogic.Period;
import main.Ticket.Ticket;
import main.Ticket.TicketFactory;

import java.util.ArrayList;
import java.util.List;

public class CreateMilestoneCommand extends BaseCommand {
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER);
    }

    public CreateMilestoneCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }

    @Override
    protected void executeLogic() {
        List<Integer> tickets = new ArrayList<>();
        if (command.has("tickets")) {
            for (JsonNode ticketId : command.get("tickets")) {
                tickets.add(ticketId.asInt());
            }
        }
        for (Milestone existing  : database.getMilestones()) {
            for (Integer ticketId : existing.getTickets()) {
                if (tickets.contains(ticketId)) {
                    throw new TicketAlreadyAssignedException("Tickets " + ticketId + " already assigned to milestone " + existing.getName() + ".");
                }
            }
        }
        Milestone newMilestone = new Milestone(command);
        database.addMilestone(newMilestone);
    }
}
