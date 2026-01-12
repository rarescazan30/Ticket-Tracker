package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Enums.RoleType;
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
        // TODO: first check if there are milestones active once I finish implementing Milestones

        Period.getInstance().startTestingPeriod(this.timestamp);
    }
}
