package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Comments.Comment;
import main.Database.Database;
import main.Enums.RoleType;
import main.Exceptions.CommentOnAnonymousTicket;
import main.Ticket.Ticket;
import main.Users.User;

import java.util.List;

/**
 * * Command responsible for reverting the last comment added by a user
 * Removes the most recent comment made by the user on a specific ticket
 * */
public final class UndoAddCommentCommand extends BaseCommand {

    /**
     * * Returns the roles allowed to execute this command
     * Both Reporters and Developers can undo their comments
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.REPORTER, RoleType.DEVELOPER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public UndoAddCommentCommand(final List<ObjectNode> outputs, final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the logic to remove the last comment
     * Iterates backwards through comments to find and remove the user's last entry
     * */
    @Override
    public void executeLogic() {
        int ticketId = command.get("ticketID").asInt();
        Ticket ticket = null;
        for (Ticket iterateTicket : Database.getInstance().getTickets()) {
            if (ticketId == iterateTicket.getId()) {
                ticket = iterateTicket;
                break;
            }
        }
        if (ticket != null) {
            User user = Database.getInstance().getUser(username);
            RoleType userRole = user.getRole();

            if (ticket.getReportedBy().equals("ANONIM")
                    || ticket.getReportedBy().isEmpty()) {
                throw new CommentOnAnonymousTicket(
                        "Comments are not allowed on anonymous tickets.");
            }

            List<Comment> comments = ticket.getComments();
            for (int i = comments.size() - 1; i >= 0; i--) {
                Comment c = comments.get(i);
                // remove last comment added by this user
                if (c.getAuthor().equals(this.username)) {
                    comments.remove(i);
                    break;
                }
            }
        }
    }
}
