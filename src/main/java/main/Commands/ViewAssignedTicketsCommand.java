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

/**
 * * Command responsible for retrieving tickets assigned to the logged-in developer
 * Filters tickets by assignee, sorts them by priority and date, and formats the output
 * */
public final class ViewAssignedTicketsCommand extends BaseCommand {

    /**
     * * Returns the roles allowed to execute this command
     * Only Developers can view their own assigned tickets
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.DEVELOPER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public ViewAssignedTicketsCommand(final List<ObjectNode> outputs, final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the logic to fetch assigned tickets
     * Filters by username, sorts by Business Priority (desc), then CreatedAt, then ID
     * */
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
