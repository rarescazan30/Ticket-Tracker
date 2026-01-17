package main.Commands;

import main.Database.Database;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Exceptions.ClosedTickeException;
import main.Exceptions.CommentLengthException;
import main.Exceptions.CommentOnAnonymousTicket;
import main.Exceptions.InvalidReporterException;
import main.Exceptions.InvalidTicketAssignmentException;
import main.Ticket.Ticket;
import main.Users.User;

/**
 * * Validator responsible for checking if a comment can be posted
 * Enforces business rules regarding user roles, ticket status, and content length
 * */
public final class CommentValidator {

    private static final int MIN_COMMENT_LENGTH = 10;
    /**
     * * Validates the comment against all business rules
     * */
    public void validate(final Ticket ticket, final String username, final String comment) {
        User user = Database.getInstance().getUser(username);
        RoleType userRole = getRoleType(ticket, username, user);

        if (comment.length() < MIN_COMMENT_LENGTH) {
            throw new CommentLengthException("Comment must be at least 10 characters long.");
        }

        if (userRole == RoleType.REPORTER) {
            if (!username.equals(ticket.getReportedBy())) {
                throw new InvalidReporterException("Reporter " + username
                        + " cannot comment on ticket " + ticket.getId() + ".");
            }
        }

        if (userRole == RoleType.DEVELOPER) {
            if (!username.equals(ticket.getAssignedTo())) {
                throw new InvalidTicketAssignmentException("Ticket " + ticket.getId()
                        + " is not assigned to the developer " + username + ".");
            }
        }
    }

    /**
     * * Determines the user's role and performs role-based checks
     * */
    private static RoleType getRoleType(final Ticket ticket,
                                        final String username, final User user) {
        RoleType userRole = user.getRole();

        if (ticket.getReportedBy().equals("ANONIM") || ticket.getReportedBy().isEmpty()) {
            throw new CommentOnAnonymousTicket("Comments are not allowed on anonymous tickets.");
        }

        if (ticket.getStatus() == StatusType.CLOSED && userRole == RoleType.REPORTER) {
            throw new ClosedTickeException("Reporters cannot comment on CLOSED tickets.");
        }

        if (userRole == RoleType.DEVELOPER) {
            if (!username.equals(ticket.getAssignedTo())) {
                throw new InvalidTicketAssignmentException("Ticket " + ticket.getId()
                        + " is not assigned to the developer " + username + ".");
            }
        }
        return userRole;
    }
}
