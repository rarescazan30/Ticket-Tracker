package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Output.OutputBuilder;
import main.Ticket.Ticket;
import main.Visitor.ImpactVisitor;
import main.Visitor.TicketVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * * Command responsible for generating a report on customer impact
 * Aggregates ticket statistics and calculates impact scores by ticket type
 * */
public final class GenerateCustomerImpactReportCommand extends BaseCommand {

    private static final double ROUNDING_FACTOR = 100.0;

    /**
     * * Returns the allowed roles for this command
     * Only Managers can generate this report
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public GenerateCustomerImpactReportCommand(final List<ObjectNode> outputs,
                                               final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the logic to generate the impact report
     * Collects statistics on open tickets and calculates average impact scores
     * */
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

        TicketVisitor impactVisitor = new ImpactVisitor();

        for (Ticket t : tickets) {
            if (t.getStatus() == StatusType.OPEN || t.getStatus() == StatusType.IN_PROGRESS) {
                totalTickets++;

                String prio = t.getBusinessPriority().toString();
                if (ticketsByPriority.containsKey(prio)) {
                    int current = ticketsByPriority.get(prio);
                    ticketsByPriority.put(prio, current + 1);
                }

                String type = t.getType();
                if (ticketsByType.containsKey(type)) {
                    int current = ticketsByType.get(type);
                    ticketsByType.put(type, current + 1);
                }

                double impactScore = t.accept(impactVisitor);
                if (scoresByType.containsKey(type)) {
                    scoresByType.get(type).add(impactScore);
                }
            }
        }

        Map<String, Double> finalImpact = new LinkedHashMap<>();
        for (String type : scoresByType.keySet()) {
            List<Double> scores = scoresByType.get(type);

            if (scores.isEmpty()) {
                finalImpact.put(type, 0.0);
            } else {
                double sum = 0;
                for (Double score : scores) {
                    sum += score;
                }
                double avg = sum / scores.size();
                finalImpact.put(type, Math.round(avg * ROUNDING_FACTOR) / ROUNDING_FACTOR);
            }
        }

        ObjectNode reportNode = mapper.createObjectNode();
        reportNode.put("totalTickets", totalTickets);
        reportNode.set("ticketsByType", mapper.valueToTree(ticketsByType));
        reportNode.set("ticketsByPriority", mapper.valueToTree(ticketsByPriority));
        reportNode.set("customerImpactByType", mapper.valueToTree(finalImpact));

        ObjectNode finalOutput = new OutputBuilder(mapper)
                .setCommand("generateCustomerImpactReport")
                .setUser(this.username)
                .setTimestamp(this.timestamp)
                .addData("report", reportNode)
                .build();

        outputs.add(finalOutput);
    }
}
