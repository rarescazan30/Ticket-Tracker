package main.Milestone;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import main.Database.Database;
import main.Enums.StatusType;
import main.Ticket.Ticket;

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

    public int getDaysUntilDue() {
        int result = ((int) ChronoUnit.DAYS.between(requestedDate, dueDate) + 1);
        if (result < 0)
            result = 0;
        return result;
    }

    public int getOverdueBy() {
        LocalDate endDate = requestedDate;
        if (getStatus().equals("COMPLETED")) {
            LocalDate maxSolvedAt = null;
            List<Ticket> allTickets = Database.getInstance().getTickets();
            for (int ticketId : tickets) {
                Ticket ticket = allTickets.get(ticketId);
                if (ticket.getSolvedAt() != null && !ticket.getSolvedAt().toString().isEmpty()) {
                    LocalDate lastSolvedDate = LocalDate.parse(ticket.getSolvedAt().toString());
                    if (maxSolvedAt == null || lastSolvedDate.isAfter(maxSolvedAt)) {
                        maxSolvedAt = lastSolvedDate;
                    }
                }

            }
            if (maxSolvedAt != null) {
                endDate = maxSolvedAt;
            }
        }
        int result;
        result = ((int) ChronoUnit.DAYS.between(dueDate, endDate) + 1);
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
}
