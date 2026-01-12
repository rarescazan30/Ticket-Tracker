package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Exceptions.InvalidPeriodException;
import main.Exceptions.InvalidRoleException;
import main.Users.User;

import java.time.LocalDate;
import java.util.List;

public abstract class BaseCommand implements Command {
    protected final Database database = Database.getInstance();
    protected final JsonNode command;
    protected final List<ObjectNode> outputs;
    protected final String username;
    protected final ObjectMapper mapper = new ObjectMapper();
    protected final String commandName;
    protected final LocalDate timestamp;

    public BaseCommand(List<ObjectNode> outputs, JsonNode command) {
        this.outputs = outputs;
        this.command = command;
        this.username = command.get("username").asText();
        this.commandName = command.get("commandName").asText();
        this.timestamp = LocalDate.parse(command.get("timestamp").asText());
    }

    protected void writeError(String error) {
        ObjectNode errorNode = mapper.createObjectNode();
        errorNode.put("command", commandName);
        errorNode.put("username", username);
        errorNode.put("timestamp", timestamp.toString());
        errorNode.put("error", error);

        outputs.add(errorNode);
    }
    protected abstract List<RoleType> getAllowedRoles();

    protected void checkRole(User user) {
        List <RoleType> allowedRoles = getAllowedRoles();
        if (!allowedRoles.isEmpty() && !allowedRoles.contains(user.getRole())) {
            StringBuilder listOfRoles = new StringBuilder();
            for (int i = 0; i < allowedRoles.size(); i++) {
                listOfRoles.append(allowedRoles.get(i).toString());
                if (i < allowedRoles.size() - 1) {
                    listOfRoles.append(", ");
                }
            }
            throw new InvalidRoleException("The user does not have permission to execute this command: " +
                    "required role " + listOfRoles.toString() + "; " +
                    "user role " + user.getRole() + ".");
        }
    }

    protected abstract void executeLogic();


    /*
    * design pattern here!!!
    * */
    @Override
    public final void execute() {
        if (username != null && database.getUser(username) == null) {
            writeError("The user " + username + " does not exist.");
            return;
        }
        User user = database.getUser(username);
        try {
            if (user != null) {
                checkRole(user);
            }
            executeLogic();
        } catch (InvalidRoleException | InvalidPeriodException e) {
            writeError(e.getMessage());
        }

    }
}
