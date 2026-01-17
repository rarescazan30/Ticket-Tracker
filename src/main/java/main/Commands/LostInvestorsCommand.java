package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Enums.RoleType;
import main.Exceptions.StopExecutionException;

import java.util.List;

/**
 * * Command responsible for halting the application execution
 * Triggered when critical business conditions (losing investors) are met
 * */
public final class LostInvestorsCommand extends BaseCommand {

    /**
     * * Returns the roles allowed to execute this command
     * Only Managers can trigger this critical stop
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public LostInvestorsCommand(final List<ObjectNode> outputs, final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the logic to stop the system
     * Throws a specific exception that signals the main loop to terminate
     * */
    @Override
    protected void executeLogic() {
        throw new StopExecutionException();
    }
}
