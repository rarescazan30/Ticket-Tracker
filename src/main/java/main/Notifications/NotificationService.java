package main.Notifications;

import main.Database.Database;
import main.Milestone.Milestone;
import main.Ticket.Ticket;
import main.Users.Developer;
import main.Users.User;

import java.util.List;

/**
 * * Service responsible for sending system notifications to developers
 * Handles messages related to milestone status changes and deadlines
 * */
public final class NotificationService {

    /**
     * * Private constructor to prevent instantiation of utility class
     * */
    private NotificationService() {
        // we never call this
    }

    private static void sendToGroup(final List<String> usernames, final String message) {
        for (String username : usernames) {
            User user = Database.getInstance().getUser(username);
            if (user instanceof Developer) {
                ((Developer) user).addNotification(message);
            }
        }
    }

    /**
     * * Notifies assigned developers when a new milestone is created
     * */
    public static void notifyMilestoneCreated(final Milestone milestone) {
        String message = "New milestone " + milestone.getName()
                + " has been created with due date " + milestone.getDueDate() + ".";

        sendToGroup(milestone.getAssignedDevs(), message);
    }

    /**
     * * Notifies developers when a milestone becomes unblocked by a ticket closure
     * @param milestone the milestone being unblocked
     * @param closedTicket the ticket that caused the unblock
     * */
    public static void notifyMilestoneUnblocked(final Milestone milestone,
                                                final Ticket closedTicket) {
        String message = "Milestone " + milestone.getName()
                + " is now unblocked as ticket " + closedTicket.getId()
                + " has been CLOSED.";

        sendToGroup(milestone.getAssignedDevs(), message);
    }

    /**
     * * Notifies developers when a milestone is unblocked after its due date
     * Indicates that active tickets have been escalated to CRITICAL
     * */
    public static void notifyMilestoneUnblockedLate(final Milestone milestone) {
        String message = "Milestone " + milestone.getName()
                + " was unblocked after due date. All active tickets are now CRITICAL.";

        sendToGroup(milestone.getAssignedDevs(), message);
    }

    /**
     * * Notifies developers that a milestone is due tomorrow
     * Warns that unresolved tickets will become CRITICAL
     * */
    public static void notifyMilestoneDueTomorrow(final Milestone milestone) {
        String message = "Milestone " + milestone.getName()
                + " is due tomorrow. All unresolved tickets are now CRITICAL.";

        sendToGroup(milestone.getAssignedDevs(), message);
    }
}
