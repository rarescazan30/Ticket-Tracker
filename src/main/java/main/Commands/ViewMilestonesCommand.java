package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Enums.RoleType;
import main.Milestone.Milestone;
import main.Output.OutputBuilder;
import main.Ticket.Ticket;
import main.Users.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ViewMilestonesCommand extends BaseCommand {
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER, RoleType.DEVELOPER);
    }

    public ViewMilestonesCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }

    @Override
    public void executeLogic() {
        User user = database.getUser(this.username);
        RoleType role = user.getRole();
        Milestone.setRequestedDate(LocalDate.parse(this.timestamp.toString()));
        List<Milestone> milestones = database.getMilestones();
        List<Milestone> resultMilestones = new ArrayList<>();
        switch(role) {
            case MANAGER:
                for (Milestone milestone : milestones) {
                    if (milestone.getCreatedBy().equals(this.username)) {
                        resultMilestones.add(milestone);
                    }
                }
                break;
            case DEVELOPER:
                for (Milestone milestone : milestones) {
                    if (milestone.getAssignedDevs().contains(this.username)) {
                        resultMilestones.add(milestone);
                    }
                }
                break;
            default:
                break;
        }
        resultMilestones.sort(Comparator.comparing(Milestone::getDueDate).thenComparing(Milestone::getName));
        ObjectNode output = new OutputBuilder(mapper).setCommand("viewMilestones").setUser(this.username).setTimestamp(this.timestamp).addData("milestones", resultMilestones).build();
        outputs.add(output);

    }
}
