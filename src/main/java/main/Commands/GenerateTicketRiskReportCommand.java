package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Output.OutputBuilder;
import main.Ticket.Ticket;
import main.Visitor.RiskVisitor;
import main.Visitor.TicketVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GenerateTicketRiskReportCommand extends BaseCommand {
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER);
    }

    public GenerateTicketRiskReportCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }

    @Override
    public void executeLogic() {
        List<Ticket> tickets = Database.getInstance().getTickets();
        int totalTickets = 0;
        Map<String, Integer> ticketsByPriority = new LinkedHashMap<>();
        ticketsByPriority.put("LOW", 0);
        ticketsByPriority.put("MEDIUM", 0);
        ticketsByPriority.put("HIGH", 0);
        ticketsByPriority.put("CRITICAL", 0);

        Map<String, Integer> ticketsByType = new LinkedHashMap<>();
        ticketsByType.put("BUG", 0);
        ticketsByType.put("FEATURE_REQUEST", 0);
        ticketsByType.put("UI_FEEDBACK", 0);

        Map<String, List<Double>> scoresByType = new LinkedHashMap<>();
        scoresByType.put("BUG", new ArrayList<>());
        scoresByType.put("FEATURE_REQUEST", new ArrayList<>());
        scoresByType.put("UI_FEEDBACK", new ArrayList<>());

        TicketVisitor riskVisitor = new RiskVisitor();

        for (Ticket t : tickets) {
            if (t.getStatus() == StatusType.OPEN || t.getStatus() == StatusType.IN_PROGRESS) {
                totalTickets++;
                String type = t.getType();
                if (ticketsByType.containsKey(type)) {
                    int currentCount = ticketsByType.get(type);
                    ticketsByType.put(type, currentCount + 1);
                } else {
                    ticketsByType.put(type, 1);
                }
                String prio = t.getBusinessPriority().toString();
                if (ticketsByPriority.containsKey(prio)) {
                    int currentCount = ticketsByPriority.get(prio);
                    ticketsByPriority.put(prio, currentCount + 1);
                } else {
                    ticketsByPriority.put(prio, 1);
                }
                double riskScore = t.accept(riskVisitor);
                if (scoresByType.containsKey(type)) {
                    scoresByType.get(type).add(riskScore);
                }
            }
        }

        Map<String, String> riskLabels = new LinkedHashMap<>();
        for (String type : scoresByType.keySet()) {
            List<Double> scores = scoresByType.get(type);
            if (scores.isEmpty()) {
                riskLabels.put(type, "NEGLIGIBLE");
            } else {
                double sum = 0;
                for (Double score : scores) {
                    sum += score;
                }
                double avg = sum / scores.size();
                riskLabels.put(type, getRiskLabel(avg));
            }
        }

        ObjectNode reportNode = mapper.createObjectNode();
        reportNode.put("totalTickets", totalTickets);
        reportNode.set("ticketsByType", mapper.valueToTree(ticketsByType));
        reportNode.set("ticketsByPriority", mapper.valueToTree(ticketsByPriority));
        reportNode.set("riskByType", mapper.valueToTree(riskLabels));

        ObjectNode finalOutput = new OutputBuilder(mapper)
                .setCommand("generateTicketRiskReport")
                .setUser(this.username)
                .setTimestamp(this.timestamp)
                .addData("report", reportNode)
                .build();

        outputs.add(finalOutput);
    }

    private String getRiskLabel(double score) {
        if (score < 25) return "NEGLIGIBLE";
        if (score < 50) return "MODERATE";
        if (score < 75) return "SIGNIFICANT";
        return "MAJOR"; // 75-100
    }
}