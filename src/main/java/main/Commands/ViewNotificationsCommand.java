package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Output.OutputBuilder;
import main.Users.Developer;
import main.Users.User;

import java.util.List;

/**
 * * Command responsible for retrieving and clearing user notifications
 * Implements a "read-once" mechanism where notifications are removed after viewing
 * */
public final class ViewNotificationsCommand extends BaseCommand {

    /**
     * * Returns the roles allowed to execute this command
     * Only Developers currently utilize the notification system
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.DEVELOPER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public ViewNotificationsCommand(final List<ObjectNode> outputs, final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the notification retrieval logic
     * Fetches pending notifications for the developer and clears the queue
     * */
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
