package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Enums.RoleType;
import main.Milestone.Milestone;
import main.Output.OutputBuilder;
import main.Ticket.Ticket;
import main.Users.User;

import java.util.List;

/**
 * * Command responsible for retrieving and displaying tickets
 * Filters tickets based on the user's role permissions using polymorphism
 * */
public final class ViewTicketsCommand extends BaseCommand {

    /**
     * * Returns the roles allowed to execute this command
     * All roles (Manager, Developer, Reporter) have access, but see different data
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER, RoleType.DEVELOPER, RoleType.REPORTER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public ViewTicketsCommand(final List<ObjectNode> outputs, final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the logic to fetch and filter tickets
     * Delegates the filtering logic to the User object to avoid switch statements
     * */
    @Override
    public void executeLogic() {
        User user = database.getUser(this.username);
        List<Ticket> tickets = database.getTickets();
        List<Milestone> milestones = database.getMilestones();
        List<Ticket> resultTickets = user.filterVisibleTickets(tickets, milestones);
        ObjectNode output = new OutputBuilder(mapper)
                .setCommand("viewTickets")
                .setUser(this.username)
                .setTimestamp(this.timestamp)
                .addData("tickets", resultTickets)
                .build();

        outputs.add(output);
    }
}
