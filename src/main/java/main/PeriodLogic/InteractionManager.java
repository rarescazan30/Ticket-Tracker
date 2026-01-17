package main.PeriodLogic;

import main.Database.Database;
import main.Enums.BusinessPriorityType;
import main.Enums.StatusType;
import main.Milestone.Milestone;
import main.Notifications.NotificationService;
import main.Ticket.Ticket;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Manages automated interactions between milestones and tickets as time passes
 * Implements Singleton and Observer (TimeObserver) patterns
 */
public final class InteractionManager implements TimeObserver {
    private static final int PRIORITY_UPGRADE_INTERVAL = 3;
    private static InteractionManager singleton;

    private InteractionManager() {
        // we leave empty to avoid instantiation
    }

    /**
     * Returns the singleton instance of InteractionManager
     */
    public static InteractionManager getInstance() {
        if (singleton == null) {
            singleton = new InteractionManager();
        }
        return singleton;
    }

    /**
     * Resets the singleton instance to null
     */
    public static void reset() {
        singleton = null;
    }

    @Override
    public void onDayPassed(final LocalDate currentDate) {
        handleMilestoneInteractions(currentDate);
    }

    /**
     * Processes milestone logic for the current date including priority upgrades
     * Calculates and decides whether ticket priority should be updated or milestone
     * tickets shyould all become critical (1 day until deadline)
     */
    private void handleMilestoneInteractions(final LocalDate currentDate) {
        Database db = Database.getInstance();
        for (Milestone milestone : db.getMilestones()) {
            LocalDate created = LocalDate.parse(milestone.getCreatedAt());
            LocalDate due = LocalDate.parse(milestone.getDueDate());
            int daysSinceCreation = (int) (ChronoUnit.DAYS.between(created, currentDate));
            int daysUntilDue = (int) (ChronoUnit.DAYS.between(currentDate, due) + 1);
            if (!milestone.getIsBlocked()) {
                if (daysSinceCreation > 0 && daysSinceCreation % PRIORITY_UPGRADE_INTERVAL == 0) {
                    updateTicketPriority(milestone, currentDate);
                }
                // 1 day to deadline, but check is 2 because of the way we calculate
                // we take the current day + the deadline day (tomorrow) into account
                if (daysUntilDue == 2) {
                    setCriticalAndNotify(milestone, db);
                }
            }
        }
    }

    /**
     * Upgrades the priority for all tickets associated with a milestone
     * @param milestone the milestone whose tickets will be updated
     * @param currentDate the current system date
     */
    private void updateTicketPriority(final Milestone milestone, final LocalDate currentDate) {
        Database db = Database.getInstance();
        List<Ticket> tickets = db.getTickets();

        for (Integer id : milestone.getTickets()) {
            Ticket ticket = tickets.get(id);
            ticket.upgradePriority(currentDate);
        }
    }

    /**
     * Sets tickets to critical priority and sends notifications for nearing deadlines
     * @param milestone the milestone reaching its deadline
     * @param db the database instance containing tickets
     */
    private void setCriticalAndNotify(final Milestone milestone, final Database db) {
        List<Ticket> tickets = db.getTickets();
        for (Integer id : milestone.getTickets()) {
            Ticket ticket = tickets.get(id);
            boolean isNotFinished = ticket.getStatus() != StatusType.CLOSED
                    && ticket.getStatus() != StatusType.RESOLVED;
            if (isNotFinished) {
                ticket.setBusinessPriority(BusinessPriorityType.CRITICAL);
            }
        }
        NotificationService.notifyMilestoneDueTomorrow(milestone);
    }
}
