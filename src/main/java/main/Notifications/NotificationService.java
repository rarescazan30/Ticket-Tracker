package main.Notifications;

import main.Database.Database;
import main.Milestone.Milestone;
import main.Ticket.Ticket;
import main.Users.Developer;
import main.Users.User;

import java.util.List;

public class NotificationService {

    private static void sendToGroup(List<String> usernames, String message) {
        for (String username : usernames) {
            User user = Database.getInstance().getUser(username);
            if (user instanceof Developer) {
                ((Developer) user).addNotification(message);
            }
        }
    }
    public static void notifyMilestoneCreated(Milestone milestone) {
        String message = "New milestone " + milestone.getName() +
                " has been created with due date " + milestone.getDueDate() + ".";

        sendToGroup(milestone.getAssignedDevs(), message);
    }
    public static void notifyMilestoneUnblocked(Milestone milestone, Ticket closedTicket) {
        String message = "Milestone " + milestone.getName() +
                " is now unblocked as ticket " + closedTicket.getId() +
                " has been CLOSED.";

        sendToGroup(milestone.getAssignedDevs(), message);
    }
    public static void notifyMilestoneUnblockedLate(Milestone milestone) {
        String message = "Milestone " + milestone.getName() +
                " was unblocked after due date. All active tickets are now CRITICAL.";

        sendToGroup(milestone.getAssignedDevs(), message);
    }
    public static void notifyMilestoneDueTomorrow(Milestone milestone) {
        String message = "Milestone " + milestone.getName() +
                " is due tomorrow. All unresolved tickets are now CRITICAL.";

        sendToGroup(milestone.getAssignedDevs(), message);
    }
}