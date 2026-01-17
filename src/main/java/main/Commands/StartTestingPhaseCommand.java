package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Exceptions.BlockedMilestoneException;
import main.Milestone.Milestone;
import main.PeriodLogic.Period;

import java.util.List;

public class StartTestingPhaseCommand extends BaseCommand {

    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER);
    }

    public StartTestingPhaseCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }
    @Override
    public void executeLogic() {
        for (Milestone milestone : Database.getInstance().getMilestones()) {
            if (milestone.getStatus().equals("ACTIVE")) {
                throw new BlockedMilestoneException("Cannot start a new testing phase.");
            }
        }

        Period.getInstance().startTestingPeriod(this.timestamp);
    }
}
