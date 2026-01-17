package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Exceptions.BlockedMilestoneException;
import main.Exceptions.ClosedTickeException;
import main.Exceptions.CommentLengthException;
import main.Exceptions.CommentOnAnonymousTicket;
import main.Exceptions.InvalidExpertiseException;
import main.Exceptions.InvalidMilestoneAccessException;
import main.Exceptions.InvalidPeriodException;
import main.Exceptions.InvalidReporterException;
import main.Exceptions.InvalidRoleException;
import main.Exceptions.InvalidSeniorityException;
import main.Exceptions.InvalidStatusException;
import main.Exceptions.InvalidTicketAssignmentException;
import main.Exceptions.NonBugAnonymous;
import main.Exceptions.TicketAlreadyAssignedException;
import main.PeriodLogic.TimeManager;
import main.Users.User;

import java.time.LocalDate;
import java.util.List;

/**
 * * Abstract base class for all commands in the system
 * Implements the Template Method Design Pattern to enforce a standard execution flow
 * This is noticeable in the execute() and executeLogic() methods.
 * */
public abstract class BaseCommand implements Command {
    protected final Database database = Database.getInstance();
    protected final JsonNode command;
    protected final List<ObjectNode> outputs;
    protected final String username;
    protected final ObjectMapper mapper = new ObjectMapper();
    protected final String commandName;
    protected final LocalDate timestamp;

    /**
     * * Constructs a base command with common fields parsed from input
     * */
    public BaseCommand(final List<ObjectNode> outputs, final JsonNode command) {
        this.outputs = outputs;
        this.command = command;
        this.username = command.get("username").asText();
        this.commandName = command.get("command").asText();
        this.timestamp = LocalDate.parse(command.get("timestamp").asText());
    }

    /**
     * * Writes a standardized error message to the output list
     * */
    protected final void writeError(final String error) {
        ObjectNode errorNode = mapper.createObjectNode();
        errorNode.put("command", commandName);
        errorNode.put("username", username);
        errorNode.put("timestamp", timestamp.toString());
        errorNode.put("error", error);

        outputs.add(errorNode);
    }

    /**
     * * Abstract method to define which roles are permitted to execute the command
     * */
    protected abstract List<RoleType> getAllowedRoles();

    /**
     * * Validates if the user has one of the allowed roles
     * */
    protected final void checkRole(final User user) {
        List<RoleType> allowedRoles = getAllowedRoles();
        if (!allowedRoles.isEmpty() && !allowedRoles.contains(user.getRole())) {
            StringBuilder listOfRoles = new StringBuilder();
            for (int i = 0; i < allowedRoles.size(); i++) {
                listOfRoles.append(allowedRoles.get(i).toString());
                if (i < allowedRoles.size() - 1) {
                    listOfRoles.append(", ");
                }
            }
            throw new InvalidRoleException("The user does not have permission to execute this "
                    + "command: required role " + listOfRoles.toString() + "; "
                    + "user role " + user.getRole() + ".");
        }
    }

    /**
     * * Abstract method where specific command logic is implemented
     * */
    protected abstract void executeLogic();

    /**
     * * Template Method: Defines the skeleton of command execution
     * Handles synchronization, user validation, role checking, and exception management
     * */
    @Override
    public final void execute() {
        TimeManager.getInstance().sync(this.timestamp);
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
        } catch (InvalidRoleException | InvalidPeriodException | NonBugAnonymous
                 | BlockedMilestoneException | InvalidExpertiseException
                 | InvalidMilestoneAccessException | InvalidSeniorityException
                 | InvalidStatusException | TicketAlreadyAssignedException | ClosedTickeException
                 | CommentLengthException | CommentOnAnonymousTicket
                 | InvalidTicketAssignmentException | InvalidReporterException e) {
            writeError(e.getMessage());
        }
    }
}
