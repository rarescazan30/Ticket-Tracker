package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class CommandFactory {
    public static Command create(String commandName, String username, JsonNode command, List<ObjectNode> outputs) {
        switch(commandName) {
            case "reportTicket":
                return new ReportTicketCommand(outputs, username, command);
            case "viewTickets":
                return new ViewTicketsCommand(outputs, username, command);
            case "addComment":
                return new AddCommentCommand(outputs, username, command);
            case "undoAddComment":
                return new UndoAddCommentCommand(outputs, username, command);
            case "createMilestone":
                return new CreateMilestoneCommand(outputs, username, command);
            case "viewMilestones":
                return new ViewMilestonesCommand(outputs, username, command);
            case "assignTicket":
                return new AssignTicketCommand(outputs, username, command);
            case "undoAssignTicket":
                return new UndoAssignTicketCommand(outputs, username, command);
            case "viewAssignedTickets":
                return new AssignedTicketsCommand(outputs, username, command);
            case "viewTicketHistory":
                return new ViewTicketHistoryCommand(outputs, username, command);
            case "viewNotifications":
                return new ViewNotificationsCommand(outputs, username, command);
            case "search":
                return new SearchCommand(outputs, username, command);
            case "generatePerformanceReport":
                return new GeneratePerformanceReportCommand(outputs, username, command);
            case "lostInvestors":
                return new LostInvestorsCommand(outputs, username, command);
            case "startTestingPhase":
                return new StartTestingPhaseCommand(outputs, username, command);
            default:
                System.out.println("Error Command Unknown");
                return null;
        }
    }
}
