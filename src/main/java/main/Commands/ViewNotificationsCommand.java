package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Output.OutputBuilder;
import main.Users.Developer;
import main.Users.User;

import java.util.List;

public class ViewNotificationsCommand extends BaseCommand {
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.DEVELOPER);
    }
    public ViewNotificationsCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }
    @Override
    public void executeLogic() {
        User user = Database.getInstance().getUser(this.username);
        Developer developer = (Developer) user;
        List<String> notifications = developer.getNotificationsAndClear();

        ObjectNode output = new OutputBuilder(mapper)
                .setCommand("viewNotifications")
                .setUser(this.username)
                .setTimestamp(this.timestamp)
                .addData("notifications", mapper.valueToTree(notifications))
                .build();

        outputs.add(output);
    }
}
