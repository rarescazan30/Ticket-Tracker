package main.Milestone;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import main.Database.Database;
import main.Enums.BusinessPriorityType;
import main.Enums.StatusType;
import main.Notifications.NotificationService;
import main.Ticket.Ticket;
import main.Ticket.TicketAction;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

// we tell Jackson how we want to structure our output
@JsonPropertyOrder({
        "name", "blockingFor", "dueDate", "createdAt", "tickets", "assignedDevs",
        "createdBy", "status", "isBlocked", "daysUntilDue", "overdueBy",
        "openTickets", "closedTickets", "completionPercentage", "repartition"
})

public class Milestone {
    private String name;
    private List<String> blockingFor = new ArrayList<>();
    private LocalDate dueDate;
    private List<Integer> tickets = new ArrayList<>();
    private List<String> assignedDevs =  new ArrayList<>();
    private String createdBy;
    private String createdAt;
    private static LocalDate requestedDate;

    public static void setRequestedDate(LocalDate requestedDate) {
        Milestone.requestedDate = requestedDate;
    }


    public Milestone(JsonNode command) {
        this.name = command.get("name").asText();
        this.dueDate = LocalDate.parse(command.get("dueDate").asText());
        if (command.has("blockingFor")) {
            for (JsonNode blockingFor : command.get("blockingFor")) {
                this.blockingFor.add(blockingFor.asText());
            }
        }
        if (command.has("tickets")) {
            for (JsonNode tickets : command.get("tickets")) {
                this.tickets.add(tickets.asInt());
            }
        }
        if (command.has("assignedDevs")) {
            for (JsonNode assignedDevs : command.get("assignedDevs")) {
                this.assignedDevs.add(assignedDevs.asText());
            }
        }
        this.createdBy = command.get("username").asText();
        this.createdAt = command.get("timestamp").asText();
    }

    public String getName() { return name; }
    public List<String> getBlockingFor() { return blockingFor; }
    public String getDueDate() { return dueDate.toString(); }
    public String getCreatedAt() { return createdAt.toString(); }
    public List<Integer> getTickets() { return tickets; }
    public List<String> getAssignedDevs() { return assignedDevs; }
    public String getCreatedBy() { return createdBy; }
    
    public List<Integer> getOpenTickets() {
        List<Integer> openTickets = new ArrayList<>();
        List<Ticket> allTickets = Database.getInstance().getTickets();
        
        for (int number : tickets) {
            if (Database.getInstance().getTickets().size() > number) {
                Ticket ticket = allTickets.get(number);
                if (ticket.getStatus() != StatusType.CLOSED) {
                    openTickets.add(number);
                }
            }
        }
        return openTickets;
    }
    public List<Integer> getClosedTickets() {
        List<Integer> closedTickets = new ArrayList<>();
        List<Ticket> allTickets = Database.getInstance().getTickets();
        for (int number : tickets) {
            Ticket ticket = allTickets.get(number);
            if (ticket.getStatus() == StatusType.CLOSED) {
                closedTickets.add(number);
            }
        }
        return closedTickets;
    }
    public double getCompletionPercentage() {
        if (tickets.isEmpty()) return 0.00;
        int nrOfClosed = getClosedTickets().size();
        double percentage = (double) nrOfClosed / (double) tickets.size();
        return (Math.round(percentage * 100.0) / 100.0);
    }

    public String getStatus() {
        if (getOpenTickets().isEmpty()) {
            return "COMPLETED";
        }
        return "ACTIVE";
    }
    public boolean getIsBlocked() {
        List<Milestone> allMilestones = Database.getInstance().getMilestones();
        for (Milestone milestone : allMilestones) {
            if (milestone.getBlockingFor().contains(this.name)) {
                if (!milestone.getStatus().equals("COMPLETED")) {
                    return true;
                }
            }
        }
        return false;
    }
    @JsonIgnore
    public LocalDate getCompletedDate() {
        LocalDate lastCompletedDate = null;

        for (int ticketId : tickets) {
            Ticket ticket = Database.getInstance().getTickets().get(ticketId);
            if (ticket.getActions() != null) {
                for (TicketAction action : ticket.getActions()) {
                    if (action.getAction().equals("STATUS_CHANGED") && action.getTo().equals("CLOSED")) {
                        if (lastCompletedDate == null || LocalDate.parse(action.getTimestamp()).isAfter(lastCompletedDate)) {
                            lastCompletedDate = LocalDate.parse(action.getTimestamp());
                        }
                    }
                }
            }
        }
        return lastCompletedDate;
    }
    public int getDaysUntilDue() {
        if (getStatus().equals("COMPLETED")) {
            LocalDate completedDate = getCompletedDate();
            if (completedDate != null) {
                int result = ((int) ChronoUnit.DAYS.between(completedDate, dueDate) + 1);
                if (result < 0)
                    result = 0;
                return result;
            }
        }
        int result = ((int) ChronoUnit.DAYS.between(requestedDate, dueDate) + 1);
        if (result < 0)
            result = 0;
        return result;
    }

    public int getOverdueBy() {
        LocalDate endDate = requestedDate;
        if (getStatus().equals("COMPLETED")) {
            LocalDate maxClosedDate = getCompletedDate();
            if (maxClosedDate != null) {
                endDate = maxClosedDate;
            }
        }
        int result = (int) ChronoUnit.DAYS.between(dueDate, endDate) + 1;
        if (result < 0) {
            result = 0;
        }
        return result;
    }

    public List<Map<String, Object>> getRepartition() {
        List<Map<String, Object>> repartition = new ArrayList<>();
        List<Ticket> allTickets = Database.getInstance().getTickets();
        for (String dev : this.assignedDevs) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("developer", dev);
            List<Integer> devTickets = new ArrayList<>();
            for (Integer ticketIt : tickets) {
                Ticket ticket = allTickets.get(ticketIt);
                if (dev.equals(ticket.getAssignedTo())) {
                    devTickets.add(ticketIt);
                }
            }
            map.put("assignedTickets", devTickets);
            repartition.add(map);
        }

        repartition.sort((m1, m2) -> {
            List<?> t1 = (List<?>) m1.get("assignedTickets");
            List<?> t2 = (List<?>) m2.get("assignedTickets");

            int sizeCompare = Integer.compare(t1.size(), t2.size());
            if (sizeCompare != 0) {
                return sizeCompare;
            }
            return ((String) m1.get("developer")).compareTo((String) m2.get("developer"));
        });

        return repartition;
    }

    public void resolvePostCompletionActions(Ticket lastClosedTicket, String currentTimestamp) {
        List<Ticket> allTickets = Database.getInstance().getTickets();
        for (int tId : this.tickets) {
            Ticket t = allTickets.get(tId);
            if (t.getStatus() != StatusType.CLOSED) {
                return;
            }
        }
        if (this.blockingFor.isEmpty()) return;

        LocalDate commandDate = LocalDate.parse(currentTimestamp);
        for (String blockedName : this.blockingFor) {
            Milestone blockedMilestone = null;
            for (Milestone m : Database.getInstance().getMilestones()) {
                if (m.getName().equals(blockedName)) {
                    blockedMilestone = m;
                    break;
                }
            }
            if (blockedMilestone == null) continue;

            LocalDate dueDate = LocalDate.parse(blockedMilestone.getDueDate());
            boolean isLate = commandDate.isAfter(dueDate);

            if (isLate) {
                for (int tId : blockedMilestone.getTickets()) {
                    if (tId < allTickets.size()) {
                        Ticket t = allTickets.get(tId);
                        if (t.getStatus() != StatusType.CLOSED) {
                            t.setBusinessPriority(BusinessPriorityType.CRITICAL);
                        }
                    }
                }
                NotificationService.notifyMilestoneUnblockedLate(blockedMilestone);
            } else {
                NotificationService.notifyMilestoneUnblocked(blockedMilestone, lastClosedTicket);
            }
        }
    }
}
