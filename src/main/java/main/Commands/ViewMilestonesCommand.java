package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Enums.RoleType;
import main.Milestone.Milestone;
import main.Output.OutputBuilder;
import main.Users.User;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * * Command responsible for retrieving and displaying milestones
 * Filters milestones based on the user's role using polymorphic behavior
 * */
public final class ViewMilestonesCommand extends BaseCommand {

    /**
     * * Returns the roles allowed to execute this command
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER, RoleType.DEVELOPER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public ViewMilestonesCommand(final List<ObjectNode> outputs, final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the logic to fetch and filter milestones
     * Delegates filtering logic to the specific User subclass
     * */
    @Override
    public void executeLogic() {
        User user = database.getUser(this.username);
        Milestone.setRequestedDate(LocalDate.parse(this.timestamp.toString()));

        List<Milestone> allMilestones = database.getMilestones();

        List<Milestone> resultMilestones = user.filterVisibleMilestones(allMilestones);

        resultMilestones.sort(Comparator.comparing(Milestone::getDueDate)
                .thenComparing(Milestone::getName));

        ObjectNode output = new OutputBuilder(mapper)
                .setCommand("viewMilestones")
                .setUser(this.username)
                .setTimestamp(this.timestamp)
                .addData("milestones", resultMilestones)
                .build();
        outputs.add(output);
    }
}
