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

import java.sql.Array;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GeneratePerformanceReportCommand extends BaseCommand {
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER);
    }
    public GeneratePerformanceReportCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }

    @Override
    public void executeLogic() {
        // we make use of specific functions to get the month before
        // this is useful and easier because we already store dates as LocalDate
        YearMonth targetMonth = YearMonth.from(this.timestamp).minusMonths(1);
        LocalDate startOfRange = targetMonth.atDay(1);
        LocalDate endOfRange = targetMonth.atEndOfMonth();

        Manager manager = (Manager)Database.getInstance().getUser(this.username);
        List<Developer> devs = new ArrayList<>();
        for (User user : Database.getInstance().getUsers()) {
            if (user.getRole() == RoleType.DEVELOPER && manager.getSubordinates().contains(user.getUsername())) {
                devs.add((Developer)user);
            }
        }
        devs.sort(Comparator.comparing(User::getUsername));
        ArrayNode reportArray = mapper.createArrayNode();

        for (Developer dev : devs) {
            int closedTicketsCount = 0;
            int bugCount = 0;
            int featureCount = 0;
            int uiCount = 0;
            int highPriorityCount = 0;
            double totalResolutionDays = 0.0;

            for (Ticket ticket : Database.getInstance().getTickets()) {
                if (!ticket.getAssignedTo().equals(dev.getUsername())) {
                    continue;
                }
                if (ticket.getStatus() != StatusType.CLOSED) {
                    continue;
                }
                if (ticket.getSolvedAt() == null) {
                    continue;
                }
                if (ticket.getSolvedAt().toString().isEmpty()) {
                    continue;
                }
                LocalDate solvedDate = LocalDate.parse(ticket.getSolvedAt().toString());

                if (!solvedDate.isBefore(startOfRange) && !solvedDate.isAfter(endOfRange)) {
                    if (ticket.getAssignedAt() == null) {
                        continue;
                    }

                    String type = ticket.getType();
                    if (type.equals("BUG")) {
                        bugCount++;
                    }
                    else if (type.equals("FEATURE_REQUEST")) {
                        featureCount++;
                    }
                    else if (type.equals("UI_FEEDBACK")) {
                        uiCount++;
                    }
                    if (ticket.getBusinessPriority() == BusinessPriorityType.HIGH ||
                            ticket.getBusinessPriority() == BusinessPriorityType.CRITICAL) {
                        highPriorityCount++;
                    }
                    if (ticket.getAssignedAt().toString().isEmpty()) {
                        continue;
                    }
                    System.out.println("For the ticket with the name and id " + ticket.getId() + " i added a ticket");

                    LocalDate assignedDate = LocalDate.parse(ticket.getAssignedAt().toString());

                    long days = ChronoUnit.DAYS.between(assignedDate, solvedDate);
                    double calculatedDays = (double) (days);

                    totalResolutionDays += (calculatedDays);
                    closedTicketsCount++;

                }
                System.out.println("For the ticket with the name and id " + ticket.getId() + " total tickets are " + closedTicketsCount);
            }
            double avgTime = (totalResolutionDays / closedTicketsCount) + 1;

            PerformanceData stats = new PerformanceData(
                    closedTicketsCount,
                    bugCount,
                    featureCount,
                    uiCount,
                    highPriorityCount,
                    avgTime
            );

            PerformanceVisitor visitor = new PerformanceVisitor(stats);
            double score = dev.accept(visitor);

            score = Math.round(score * 100.0) / 100.0;
            avgTime = Math.round(avgTime * 100.0) / 100.0;

            dev.setPerformanceScore(score);

            ObjectNode devNode = mapper.createObjectNode();
            devNode.put("username", dev.getUsername());
            devNode.put("closedTickets", closedTicketsCount);
            devNode.put("averageResolutionTime", avgTime);
            devNode.put("performanceScore", score);
            devNode.put("seniority", dev.getSeniority().toString());

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
}
