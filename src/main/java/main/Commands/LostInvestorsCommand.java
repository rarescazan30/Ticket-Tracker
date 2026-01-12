package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Enums.RoleType;
import main.Exceptions.StopExecutionException;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.List;

public class LostInvestorsCommand extends BaseCommand {
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER);
    }
    public LostInvestorsCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }

    @Override
    protected void executeLogic() {
        throw new StopExecutionException();
    }
}
