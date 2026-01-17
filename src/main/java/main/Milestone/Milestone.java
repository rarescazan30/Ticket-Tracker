package main.Milestone;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import main.Database.Database;
import main.Enums.StatusType;
import main.Ticket.Ticket;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Represents a project milestone containing a set of tickets and assigned developers
 */
// we tell Jackson how we want to structure our output
@JsonPropertyOrder({
        "name", "blockingFor", "dueDate", "createdAt", "tickets", "assignedDevs",
        "createdBy", "status", "isBlocked", "daysUntilDue", "overdueBy",
        "openTickets", "closedTickets", "completionPercentage", "repartition"
})
public final class Milestone {
    private String name;
    private List<String> blockingFor = new ArrayList<>();
    private LocalDate dueDate;
    private List<Integer> tickets = new ArrayList<>();
    private List<String> assignedDevs = new ArrayList<>();
    private String createdBy;
    private String createdAt;
    private static LocalDate requestedDate;

    /**
     * Sets the global requested date for date calculations
     */
    public static void setRequestedDate(final LocalDate requestedDate) {
        Milestone.requestedDate = requestedDate;
    }

    /**
     * Constructs a new milestone by parsing data from a JSON command node
     */
    public Milestone(final JsonNode command) {
        this.name = command.get("name").asText();
        this.dueDate = LocalDate.parse(command.get("dueDate").asText());
        if (command.has("blockingFor")) {
            for (JsonNode blockingNode : command.get("blockingFor")) {
                this.blockingFor.add(blockingNode.asText());
            }
        }
        if (command.has("tickets")) {
            for (JsonNode ticketNode : command.get("tickets")) {
                int ticketId = ticketNode.asInt();
                if (ticketId < Database.getInstance().getTickets().size()) {
                    this.tickets.add(ticketId);
                }
            }
        }
        if (command.has("assignedDevs")) {
            for (JsonNode devNode : command.get("assignedDevs")) {
                this.assignedDevs.add(devNode.asText());
            }
        }
        this.createdBy = command.get("username").asText();
        this.createdAt = command.get("timestamp").asText();
    }

    /**
     * Returns the name of the milestone
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of milestone names that this milestone is blocking
     */
    public List<String> getBlockingFor() {
        return blockingFor;
    }

    /**
     * Returns the due date as a string
     */
    public String getDueDate() {
        return dueDate.toString();
    }

    /**
     * Returns the creation date as a string
     */
    public String getCreatedAt() {
        return createdAt.toString();
    }

    /**
     * Returns the list of ticket IDs associated with this milestone
     */
    public List<Integer> getTickets() {
        return tickets;
    }

    /**
     * Returns the list of usernames for developers assigned to this milestone
     */
    public List<String> getAssignedDevs() {
        return assignedDevs;
    }

    /**
     * Returns the username of the creator
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Returns the IDs of tickets that are not yet closed
     */
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

    /**
     * Returns the IDs of tickets that are closed
     */
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

    /**
     * Calculates completion percentage using the Statistics utility
     */
    public double getCompletionPercentage() {
        return MilestoneStatistics.getCompletionPercentage(this);
    }

    /**
     * Returns the current status of the milestone
     */
    public String getStatus() {
        if (getOpenTickets().isEmpty()) {
            return "COMPLETED";
        }
        return "ACTIVE";
    }

    /**
     * Checks if this milestone is blocked by any other incomplete milestone
     */
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

    /**
     * Calculates days until due using the Statistics utility
     */
    public int getDaysUntilDue() {
        return MilestoneStatistics.getDaysUntilDue(this, requestedDate);
    }

    /**
     * Calculates overdue days using the Statistics utility
     */
    public int getOverdueBy() {
        return MilestoneStatistics.getOverdueBy(this, requestedDate);
    }

    /**
     * Generates a list of developer assignments sorted by ticket count and then by username
     * @return a list of maps with developer names and their assigned ticket IDs
     */
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

    /**
     * Delegates post-completion actions to the MilestoneService
     * @param lastClosedTicket the ticket that triggered the completion
     * @param currentTimestamp the time when the action occurred
     */
    public void resolvePostCompletionActions(final Ticket lastClosedTicket,
                                             final String currentTimestamp) {
        MilestoneService.resolvePostCompletionActions(this, lastClosedTicket, currentTimestamp);
    }
}
