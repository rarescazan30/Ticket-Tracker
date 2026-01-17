package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Comments.Comment;
import main.Database.Database;
import main.Enums.RoleType;
import main.Ticket.Ticket;

import java.util.List;

/**
 * * Command responsible for adding a comment to a ticket
 * Validates the user and content before appending the comment
 * */
public final class AddCommentCommand extends BaseCommand {

    /**
     * * Returns the roles allowed to execute this command
     * Both Reporters and Developers can add comments
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.REPORTER, RoleType.DEVELOPER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public AddCommentCommand(final List<ObjectNode> outputs, final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the logic to find the ticket and add the comment
     * Uses CommentValidator to ensure business rules are respected
     * */
    @Override
    public void executeLogic() {
        int ticketId = command.get("ticketID").asInt();
        Ticket ticket = null;
        for (Ticket iterateTicket : Database.getInstance().getTickets()) {
            if (ticketId == iterateTicket.getId()) {
                ticket = iterateTicket;
            }
        }

        if (ticket != null) {
            String username = command.get("username").asText();
            String comment = command.get("comment").asText();

            CommentValidator validator = new CommentValidator();
            validator.validate(ticket, username, comment);

            Comment newComment = new Comment(comment, username, this.timestamp.toString());
            ticket.getComments().add(newComment);
        }
    }
}
