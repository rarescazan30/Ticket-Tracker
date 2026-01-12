package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Enums.RoleType;
import main.Output.OutputBuilder;
import main.Ticket.Ticket;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ViewAssignedTicketsCommand extends BaseCommand {
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.DEVELOPER);
    }
    public ViewAssignedTicketsCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }

    @Override
    protected void executeLogic() {
        List<Ticket> assignedTickets = database.getTickets().stream()
                .filter(ticket -> this.username.equals(ticket.getAssignedTo()))
                .collect(Collectors.toList());
        assignedTickets.sort(Comparator.comparing(Ticket::getBusinessPriority).reversed()
                .thenComparing(Ticket::getCreatedAt)
                .thenComparing(Ticket::getId));


        ArrayNode ticketsNode = mapper.createArrayNode();
        // we remove what ref doesn't print for tickets when calling this command
        for (Ticket t : assignedTickets) {
            ObjectNode tNode = mapper.valueToTree(t);
            tNode.remove("assignedTo");
            tNode.remove("solvedAt");

            ticketsNode.add(tNode);
        }

        ObjectNode output = new OutputBuilder(mapper)
                .setCommand("viewAssignedTickets")
                .setUser(this.username)
                .setTimestamp(this.timestamp)
                .addData("assignedTickets", ticketsNode)
                .build();
        outputs.add(output);
    }
}
