package main.Milestone;

import main.Database.Database;
import main.Enums.BusinessPriorityType;
import main.Enums.StatusType;
import main.Notifications.NotificationService;
import main.Ticket.Ticket;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service class for handling milestone workflow actions and post-completion logic
 */
public final class MilestoneService {

    private MilestoneService() {
        // utility class
    }

    /**
     * Orchestrates actions that occur after a milestone is completed
     * @param milestone the completed milestone
     * @param lastClosedTicket the ticket that triggered the completion
     * @param currentTimestamp the time when the action occurred
     */
    public static void resolvePostCompletionActions(final Milestone milestone,
                                                    final Ticket lastClosedTicket,
                                                    final String currentTimestamp) {
        if (!isMilestoneFullyClosed(milestone)) {
            return;
        }

        if (milestone.getBlockingFor().isEmpty()) {
            return;
        }

        processBlockedMilestones(milestone, lastClosedTicket, currentTimestamp);
    }

    /**
     * Checks if all tickets within a milestone are closed
     */
    private static boolean isMilestoneFullyClosed(final Milestone milestone) {
        List<Ticket> allTickets = Database.getInstance().getTickets();
        for (int tId : milestone.getTickets()) {
            if (allTickets.get(tId).getStatus() != StatusType.CLOSED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Processes all milestones blocked by the completed one
     * @param milestone the milestone that was just completed
     * @param lastClosedTicket the ticket that caused the completion
     * @param currentTimestamp the current system time
     */
    private static void processBlockedMilestones(final Milestone milestone,
                                                 final Ticket lastClosedTicket,
                                                 final String currentTimestamp) {
        LocalDate commandDate = LocalDate.parse(currentTimestamp);
        for (String blockedName : milestone.getBlockingFor()) {
            Milestone blockedMilestone = findMilestoneByName(blockedName);
            if (blockedMilestone == null) {
                continue;
            }
            handleUnblockingLogic(blockedMilestone, lastClosedTicket, commandDate);
        }
    }

    /**
     * Finds a milestone in the database by its name
     */
    private static Milestone findMilestoneByName(final String name) {
        for (Milestone m : Database.getInstance().getMilestones()) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Determines whether unblocking is late or on time and applies logic
     * @param blockedMilestone the milestone to be unblocked
     * @param lastClosedTicket the ticket that triggered the unblocking
     * @param commandDate the date of the current operation
     */
    private static void handleUnblockingLogic(final Milestone blockedMilestone,
                                              final Ticket lastClosedTicket,
                                              final LocalDate commandDate) {
        LocalDate dueDate = LocalDate.parse(blockedMilestone.getDueDate());
        boolean isLate = commandDate.isAfter(dueDate);

        if (isLate) {
            applyLateUnblocking(blockedMilestone);
        } else {
            applyOnTimeUnblocking(blockedMilestone, lastClosedTicket, commandDate, dueDate);
        }
    }

    /**
     * Increases priority for tickets in a milestone unblocked after its due date
     */
    private static void applyLateUnblocking(final Milestone blockedMilestone) {
        List<Ticket> allTickets = Database.getInstance().getTickets();
        for (int tId : blockedMilestone.getTickets()) {
            if (tId < allTickets.size()) {
                Ticket t = allTickets.get(tId);
                if (t.getStatus() != StatusType.CLOSED) {
                    t.setBusinessPriority(BusinessPriorityType.CRITICAL);
                }
            }
        }
        NotificationService.notifyMilestoneUnblockedLate(blockedMilestone);
    }

    /**
     * Notifies and checks urgency for milestones unblocked before or on due date
     * @param blockedMilestone the milestone being unblocked
     * @param lastClosedTicket the ticket triggering the action
     * @param commandDate the current date of the action
     * @param dueDate the due date of the blocked milestone
     */
    private static void applyOnTimeUnblocking(final Milestone blockedMilestone,
                                              final Ticket lastClosedTicket,
                                              final LocalDate commandDate,
                                              final LocalDate dueDate) {
        NotificationService.notifyMilestoneUnblocked(blockedMilestone, lastClosedTicket);

        int daysUntilDue = (int) (ChronoUnit.DAYS.between(commandDate, dueDate) + 1);
        if (daysUntilDue == 2) {
            List<Ticket> allTickets = Database.getInstance().getTickets();
            for (int tId : blockedMilestone.getTickets()) {
                if (tId < allTickets.size()) {
                    Ticket t = allTickets.get(tId);
                    boolean isNotFinished = t.getStatus() != StatusType.CLOSED
                            && t.getStatus() != StatusType.RESOLVED;
                    if (isNotFinished) {
                        t.setBusinessPriority(BusinessPriorityType.CRITICAL);
                    }
                }
            }
            NotificationService.notifyMilestoneDueTomorrow(blockedMilestone);
        }
    }
}
