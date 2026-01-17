package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Exceptions.BlockedMilestoneException;
import main.Milestone.Milestone;
import main.PeriodLogic.Period;

import java.util.List;

/**
 * * Command responsible for transitioning the system into the testing phase
 * Ensures no milestones are currently active before starting
 * */
public final class StartTestingPhaseCommand extends BaseCommand {

    /**
     * * Returns the roles allowed to execute this command
     * Only Managers can start the testing phase
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public StartTestingPhaseCommand(final List<ObjectNode> outputs, final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the logic to start the testing phase
     * Validates milestone status and updates the global period state
     * */
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
