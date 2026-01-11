package main.Commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;

import java.util.List;

public abstract class BaseCommand implements Command {
    protected final Database database = Database.getInstance();
    protected final List<ObjectNode> outputs;
    protected final String username;
    protected final ObjectMapper mapper = new ObjectMapper();
    protected final String commandName;
    public BaseCommand(List<ObjectNode> outputs, String username, String commandName) {
        this.outputs = outputs;
        this.username = username;
        this.commandName = commandName;
    }
    protected void writeError(String error) {
        ObjectNode errorNode = mapper.createObjectNode();
        errorNode.put("command", commandName);
        errorNode.put("username", username);
        errorNode.put("timestamp", "`TODO`");
        errorNode.put("error", error);

        outputs.add(errorNode);
    }

    protected abstract void executeLogic();

    @Override
    public final void execute() {
        if (username != null && database.getUser(username) == null) {
            writeError("The user " + username + " does not exist.");
            return;
        }
        executeLogic();
    }
}
