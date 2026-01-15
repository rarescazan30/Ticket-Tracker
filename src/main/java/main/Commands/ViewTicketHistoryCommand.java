package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Milestone.Milestone;
import main.Output.OutputBuilder;
import main.Ticket.Ticket;
import main.Ticket.TicketAction;
import main.Ticket.TicketHistoryDetails;
import main.Users.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ViewTicketHistoryCommand extends BaseCommand {
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.DEVELOPER, RoleType.MANAGER);
    }

    public ViewTicketHistoryCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }
    @Override
    public void executeLogic() {
        User user = Database.getInstance().getUser(this.username);
        List<Ticket> allTickets = Database.getInstance().getTickets();
        List<TicketHistoryDetails> output =  new ArrayList<>();
        List<Ticket> visibleTickets = new ArrayList<>();
        if (user.getRole() == RoleType.MANAGER) {
            // only tickets from his milestones
            List<Milestone> managerMilestones = new ArrayList<>();
            for (Milestone m : Database.getInstance().getMilestones()) {
                if (m.getCreatedBy().equals(this.username)) {
                    managerMilestones.add(m);
                }
            }
            for (Ticket ticket : allTickets) {
                boolean isInMilestone = false;
                for (Milestone milestone : managerMilestones) {
                    if (milestone.getTickets().contains(ticket.getId())) {
                        isInMilestone = true;
                        break;
                    }
                }
                if (isInMilestone) {
                    visibleTickets.add(ticket);
                }
            }
        } else if (user.getRole() == RoleType.DEVELOPER) {
            for  (Ticket ticket : allTickets) {
                boolean wasAssigned = false;
                // assigned and assigned to this user
                for (TicketAction action : ticket.getActions()) {
                    if (action.getAction().equals("ASSIGNED") && this.username.equals(action.getBy())) {
                        wasAssigned = true;
                        break;
                    }
                }
                if (wasAssigned) {
                    visibleTickets.add(ticket);
                }
            }
        }
        visibleTickets.sort(new Comparator<Ticket>() {
            @Override
            public int compare(Ticket t1, Ticket t2) {
                int dateComparison = t1.getCreatedAt().compareTo(t2.getCreatedAt());
                if (dateComparison != 0) {
                    return dateComparison;
                }
                // case equal, sort by id
                return Integer.compare(t1.getId(), t2.getId());
            }
        });
        for (Ticket ticket : visibleTickets) {
            List<TicketAction> processed =  new ArrayList<>();
            if (user.getRole() == RoleType.DEVELOPER) {
                for (TicketAction action : ticket.getActions()) {
                    processed.add(action);
                    // we add until he quits (if he did)
                    if (action.getAction().equals("DE-ASSIGNED") && this.username.equals(action.getBy())) {
                        break;
                    }
                }
            } else {
                // manager
                processed.addAll(ticket.getActions());
            }
            output.add(new TicketHistoryDetails(ticket, processed));
        }
        ObjectNode finalOutput = new OutputBuilder(mapper)
                .setCommand("viewTicketHistory")
                .setUser(this.username)
                .setTimestamp(this.timestamp)
                .addData("ticketHistory", output)
                .build();
        outputs.add(finalOutput);
    }
}
