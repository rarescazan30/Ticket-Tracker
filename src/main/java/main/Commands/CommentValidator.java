package main.Commands;

import main.Database.Database;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Exceptions.*;
import main.Ticket.Ticket;
import main.Users.User;

public class CommentValidator {
    public void validate(Ticket ticket, String username, String comment) {
        User user = Database.getInstance().getUser(username);
        RoleType userRole = user.getRole();

        if (ticket.getReportedBy().equals("ANONIM") || ticket.getReportedBy().isEmpty()) {
            throw new CommentOnAnonymousTicket("Comments are not allowed on anonymous tickets.");
        }

        if (ticket.getStatus() == StatusType.CLOSED && userRole == RoleType.REPORTER) {
            throw new ClosedTickeException("Reporters cannot comment on CLOSED tickets.");
        }

        if (userRole == RoleType.DEVELOPER) {
            if (!username.equals(ticket.getAssignedTo())) {
                throw new InvalidTicketAssignmentException("Ticket " + ticket.getId() + " is not assigned to the developer " + username + ".");
            }
        }


        if (comment.length() < 10) {
            throw new CommentLengthException("Comment must be at least 10 characters long.");
        }

        if (userRole == RoleType.REPORTER) {
            if (!username.equals(ticket.getReportedBy())) {
                throw new InvalidReporterException("Reporter " + username + " cannot comment on ticket " + ticket.getId() + ".");
            }
        }

        if (userRole == RoleType.DEVELOPER) {
            if (!username.equals(ticket.getAssignedTo())) {
                throw new InvalidTicketAssignmentException("Ticket " + ticket.getId() + " is not assigned to the developer " + username + ".");
            }
        }
    }
}