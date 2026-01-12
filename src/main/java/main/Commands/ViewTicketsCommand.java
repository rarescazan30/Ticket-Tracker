package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Milestone.Milestone;
import main.Output.OutputBuilder;
import main.Ticket.Ticket;
import main.Users.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViewTicketsCommand extends BaseCommand {

    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER, RoleType.DEVELOPER, RoleType.REPORTER);
    }

    public ViewTicketsCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }

    @Override
    public void executeLogic() {
        User user = database.getUser(this.username);
        RoleType role = user.getRole();

        List<Ticket> tickets = database.getTickets();
        List<Ticket> resultTickets = new ArrayList<>();
        switch(role) {
            case MANAGER:
                resultTickets.addAll(tickets);
                break;
            case DEVELOPER:
                Set<Integer> blockedTicketsIds = new HashSet<>();
                for (Milestone milestone : database.getMilestones()) {
                    if (milestone.getIsBlocked()) {
                        blockedTicketsIds.addAll(milestone.getTickets());
                    }
                }
                for (Ticket ticket : tickets) {
                    if (ticket.getStatus() == StatusType.OPEN && !blockedTicketsIds.contains(ticket.getId())) {
                        resultTickets.add(ticket);
                    }
                }
                break;
            case REPORTER:
                for (Ticket ticket : tickets) {
                    if (ticket.getReportedBy().equals(this.username)) {
                        resultTickets.add(ticket);
                    }
                }
                break;
            default:
                break;
        }

        ObjectNode output = new OutputBuilder(mapper).setCommand("viewTickets").setUser(this.username).setTimestamp(this.timestamp).addData("tickets", resultTickets).build();
        outputs.add(output);

    }
}
