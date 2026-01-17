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

public class InteractionManager implements TimeObserver {
    private static InteractionManager singleton;

    private InteractionManager() {
        // we leave empty to avoid instantiation
    }
    public static InteractionManager getInstance() {
        if (singleton == null) {
            singleton = new InteractionManager();
        }
        return singleton;
    }
    public static void reset() {
        singleton = null;
    }
    @Override
    public void onDayPassed(LocalDate currentDate) {
        handleMilestoneInteractions(currentDate);
    }

    private void handleMilestoneInteractions(LocalDate currentDate) {
        Database db = Database.getInstance();
        for (Milestone milestone : db.getMilestones()) {
            if (milestone.getStatus().equals("COMPLETED")) {
                continue;
            }
            LocalDate created = LocalDate.parse(milestone.getCreatedAt());
            LocalDate due = LocalDate.parse(milestone.getDueDate());
            int daysSinceCreation = (int) (ChronoUnit.DAYS.between(created, currentDate));
            int daysUntilDue = (int) (ChronoUnit.DAYS.between(currentDate, due) + 1);
            if (!milestone.getIsBlocked()) {
                if (daysSinceCreation > 0 && daysSinceCreation % 3 == 0) {
                    updateTicketPriority(milestone);
                }
                if (daysUntilDue == 2) {
                    setCriticalAndNotify(milestone, db);
                }
            }
        }
    }

    private void updateTicketPriority(Milestone milestone) {
        Database db = Database.getInstance();
        List<Ticket> tickets = db.getTickets();

        for (Integer id : milestone.getTickets()) {
            Ticket ticket = tickets.get(id);
            ticket.upgradePriority();
        }
    }

    private void setCriticalAndNotify(Milestone milestone, Database db) {

        List<Ticket> tickets = db.getTickets();
        for (Integer id : milestone.getTickets()) {
            Ticket ticket = tickets.get(id);
            if (ticket.getStatus() != StatusType.CLOSED && ticket.getStatus() != StatusType.RESOLVED) {
                ticket.setBusinessPriority(BusinessPriorityType.CRITICAL);
            }
        }
        NotificationService.notifyMilestoneDueTomorrow(milestone);
    }

}
