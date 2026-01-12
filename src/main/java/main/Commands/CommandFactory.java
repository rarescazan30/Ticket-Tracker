package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class CommandFactory {
    public static Command create(List<ObjectNode> outputs, JsonNode command) {
        String commandName = command.get("command").asText();
        switch(commandName) {
            case "reportTicket":
                return new ReportTicketCommand(outputs, command);
            case "viewTickets":
                return new ViewTicketsCommand(outputs, command);
//            case "addComment":
//                return new AddCommentCommand(outputs, command);
//            case "undoAddComment":
//                return new UndoAddCommentCommand(outputs, command);
            case "createMilestone":
                return new CreateMilestoneCommand(outputs, command);
            case "viewMilestones":
                return new ViewMilestonesCommand(outputs, command);
            case "assignTicket":
                return new AssignTicketCommand(outputs, command);
            case "undoAssignTicket":
                return new UndoAssignTicketCommand(outputs, command);
            case "viewAssignedTickets":
                return new ViewAssignedTicketsCommand(outputs, command);
//            case "viewTicketHistory":
//                return new ViewTicketHistoryCommand(outputs, command);
//            case "viewNotifications":
//                return new ViewNotificationsCommand(outputs, command);
//            case "search":
//                return new SearchCommand(outputs, command);
//            case "generatePerformanceReport":
//                return new GeneratePerformanceReportCommand(outputs, command);
            case "lostInvestors":
                return new LostInvestorsCommand(outputs, command);
            case "startTestingPhase":
                return new StartTestingPhaseCommand(outputs, command);
            default:
                System.out.println("Error Command Unknown");
                return null;
        }
    }
}
