package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.BusinessPriorityType;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Output.OutputBuilder;
import main.Ticket.Ticket;
import main.Users.Developer;
import main.Users.Manager;
import main.Users.User;
import main.Visitor.PerformanceData;
import main.Visitor.PerformanceVisitor;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * * Command responsible for generating performance reports for developers
 * Calculates statistics such as closed tickets, resolution time, and applies the Visitor pattern
 * */
public final class GeneratePerformanceReportCommand extends BaseCommand {

    private static final double ROUNDING_FACTOR = 100.0;

    /**
     * * Returns the roles allowed to execute this command
     * Only Managers can generate performance reports
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public GeneratePerformanceReportCommand(final List<ObjectNode> outputs,
                                            final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the logic to generate the performance report
     * Aggregates data for the previous month and calculates scores for each subordinate developer
     * We make use of the YearMonth class and its methods since we're already
     * working with LocalDates
     * */
    @Override
    public void executeLogic() {
        YearMonth targetMonth = YearMonth.from(this.timestamp).minusMonths(1);
        LocalDate startOfRange = targetMonth.atDay(1);
        LocalDate endOfRange = targetMonth.atEndOfMonth();

        Manager manager = (Manager) Database.getInstance().getUser(this.username);
        List<Developer> devs = getSubordinates(manager);
        devs.sort(Comparator.comparing(User::getUsername));

        ArrayNode reportArray = mapper.createArrayNode();

        for (Developer dev : devs) {
            ObjectNode devNode = processDeveloperPerformance(dev, startOfRange, endOfRange);
            reportArray.add(devNode);
        }

        ObjectNode finalOutput = new OutputBuilder(mapper)
                .setCommand("generatePerformanceReport")
                .setUser(this.username)
                .setTimestamp(this.timestamp)
                .addData("report", reportArray)
                .build();

        outputs.add(finalOutput);
    }

    /**
     * * Filters the list of users to find developers subordinate to the manager
     * */
    private List<Developer> getSubordinates(final Manager manager) {
        List<Developer> devs = new ArrayList<>();
        for (User user : Database.getInstance().getUsers()) {
            if (user.getRole() == RoleType.DEVELOPER
                    && manager.getSubordinates().contains(user.getUsername())) {
                devs.add((Developer) user);
            }
        }
        return devs;
    }

    /**
     * * Processes a single developer's performance for the given date range
     * Calculates stats, applies the visitor, and builds the JSON node
     * */
    private ObjectNode processDeveloperPerformance(final Developer dev,
                                                   final LocalDate start,
                                                   final LocalDate end) {
        int closedTicketsCount = 0;
        int bugCount = 0;
        int featureCount = 0;
        int uiCount = 0;
        int highPriorityCount = 0;
        double totalResolutionDays = 0.0;

        for (Ticket ticket : Database.getInstance().getTickets()) {
            if (!isValidTicketForStats(ticket, dev.getUsername(), start, end)) {
                continue;
            }

            String type = ticket.getType();
            if ("BUG".equals(type)) {
                bugCount++;
            } else if ("FEATURE_REQUEST".equals(type)) {
                featureCount++;
            } else if ("UI_FEEDBACK".equals(type)) {
                uiCount++;
            }

            if (ticket.getBusinessPriority() == BusinessPriorityType.HIGH
                    || ticket.getBusinessPriority() == BusinessPriorityType.CRITICAL) {
                highPriorityCount++;
            }

            LocalDate solvedDate = LocalDate.parse(ticket.getSolvedAt().toString());
            LocalDate assignedDate = LocalDate.parse(ticket.getAssignedAt().toString());

            long days = ChronoUnit.DAYS.between(assignedDate, solvedDate);
            totalResolutionDays += days;
            closedTicketsCount++;
        }

        double avgTime = (totalResolutionDays / closedTicketsCount) + 1;

        PerformanceData stats = new PerformanceData(
                closedTicketsCount, bugCount, featureCount, uiCount, highPriorityCount, avgTime
        );

        PerformanceVisitor visitor = new PerformanceVisitor(stats);
        double score = dev.accept(visitor);

        score = Math.round(score * ROUNDING_FACTOR) / ROUNDING_FACTOR;
        avgTime = Math.round(avgTime * ROUNDING_FACTOR) / ROUNDING_FACTOR;

        dev.setPerformanceScore(score);

        ObjectNode devNode = mapper.createObjectNode();
        devNode.put("username", dev.getUsername());
        devNode.put("closedTickets", closedTicketsCount);
        devNode.put("averageResolutionTime", avgTime);
        devNode.put("performanceScore", score);
        devNode.put("seniority", dev.getSeniority().toString());

        return devNode;
    }

    /**
     * * Validates if a ticket should be included in the statistics
     * Checks assignment, status, dates, and validity of timestamps
     * */
    private boolean isValidTicketForStats(final Ticket ticket, final String devUsername,
                                          final LocalDate start, final LocalDate end) {
        if (!ticket.getAssignedTo().equals(devUsername)) {
            return false;
        }
        if (ticket.getStatus() != StatusType.CLOSED) {
            return false;
        }
        if (ticket.getSolvedAt() == null || ticket.getSolvedAt().toString().isEmpty()) {
            return false;
        }
        if (ticket.getAssignedAt() == null || ticket.getAssignedAt().toString().isEmpty()) {
            return false;
        }

        LocalDate solvedDate = LocalDate.parse(ticket.getSolvedAt().toString());
        return !solvedDate.isBefore(start) && !solvedDate.isAfter(end);
    }
}
