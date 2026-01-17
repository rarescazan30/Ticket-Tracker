package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Exceptions.TicketAlreadyAssignedException;
import main.Milestone.Milestone;
import main.Notifications.NotificationService;
import main.Ticket.Ticket;
import main.Ticket.TicketAction;

import java.util.ArrayList;
import java.util.List;

/**
 * * Command responsible for creating a new milestone in the system
 * Validates that the tickets included are not already assigned to another milestone
 * */
public final class CreateMilestoneCommand extends BaseCommand {

    /**
     * * Returns the roles allowed to execute this command
     * Only Managers can create milestones
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public CreateMilestoneCommand(final List<ObjectNode> outputs, final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the milestone creation logic
     * Checks for ticket conflicts, adds the milestone, notifies users, and logs actions
     * */
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
                    throw new TicketAlreadyAssignedException("Tickets " + ticketId
                            + " already assigned to milestone " + existing.getName() + ".");
                }
            }
        }
        Milestone newMilestone = new Milestone(command);
        database.addMilestone(newMilestone);
        NotificationService.notifyMilestoneCreated(newMilestone);
        String milestoneName = command.get("name").asText();
        for (int ticketId : tickets) {
            if (ticketId < Database.getInstance().getTickets().size()) {
                Ticket t = Database.getInstance().getTickets().get(ticketId);
                t.addAction(new TicketAction("ADDED_TO_MILESTONE",
                        milestoneName, this.username, this.timestamp, true));
            }
        }
    }
}
