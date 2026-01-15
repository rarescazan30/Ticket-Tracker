package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Comments.Comment;
import main.Database.Database;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Exceptions.ClosedTickeException;
import main.Exceptions.CommentLengthException;
import main.Exceptions.CommentOnAnonymousTicket;
import main.Ticket.Ticket;
import main.Users.User;

import java.util.List;

public class AddCommentCommand extends BaseCommand {
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.REPORTER, RoleType.DEVELOPER);
    }

    public AddCommentCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }

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
