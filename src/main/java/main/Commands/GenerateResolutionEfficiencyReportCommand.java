package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Output.OutputBuilder;
import main.Ticket.Ticket;
import main.Visitor.EfficiencyVisitor;
import main.Visitor.TicketVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * * Command responsible for generating a resolution efficiency report
 * Aggregates statistics on resolved and closed tickets using the EfficiencyVisitor
 * */
public final class GenerateResolutionEfficiencyReportCommand extends BaseCommand {

    private static final double ROUNDING_FACTOR = 100.0;

    /**
     * * Returns the roles allowed to execute this command
     * Only Managers can view the resolution efficiency report
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public GenerateResolutionEfficiencyReportCommand(final List<ObjectNode> outputs,
                                                     final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the logic to calculate resolution efficiency
     * Iterates through resolved/closed tickets and calculates efficiency scores
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

        TicketVisitor efficiencyVisitor = new EfficiencyVisitor();

        for (Ticket t : tickets) {
            if (t.getStatus() == StatusType.RESOLVED || t.getStatus() == StatusType.CLOSED) {
                totalTickets++;

                String ticketPriority = t.getBusinessPriority().toString();
                if (ticketsByPriority.containsKey(ticketPriority)) {
                    int current = ticketsByPriority.get(ticketPriority);
                    // increment for each type/priority etc
                    ticketsByPriority.put(ticketPriority, current + 1);
                }

                String ticketType = t.getType();
                if (ticketsByType.containsKey(ticketType)) {
                    int current = ticketsByType.get(ticketType);
                    ticketsByType.put(ticketType, current + 1);
                }

                // we delegate calculations to this visitor
                double ticketEfficiencyScore = t.accept(efficiencyVisitor);
                if (scoresByType.containsKey(ticketType)) {
                    scoresByType.get(ticketType).add(ticketEfficiencyScore);
                }
            }
        }

        Map<String, Double> finalEfficiency = new LinkedHashMap<>();
        for (String type : scoresByType.keySet()) {
            List<Double> scores = scoresByType.get(type);

            if (scores.isEmpty()) {
                finalEfficiency.put(type, 0.0);
            } else {
                double sum = 0;
                for (Double score : scores) {
                    sum += score;
                }
                double avg = sum / scores.size();
                finalEfficiency.put(type, Math.round(avg * ROUNDING_FACTOR) / ROUNDING_FACTOR);
            }
        }

        ObjectNode reportNode = mapper.createObjectNode();
        reportNode.put("totalTickets", totalTickets);
        reportNode.set("ticketsByType", mapper.valueToTree(ticketsByType));
        reportNode.set("ticketsByPriority", mapper.valueToTree(ticketsByPriority));
        reportNode.set("efficiencyByType", mapper.valueToTree(finalEfficiency));

        ObjectNode finalOutput = new OutputBuilder(mapper)
                .setCommand("generateResolutionEfficiencyReport")
                .setUser(this.username)
                .setTimestamp(this.timestamp)
                .addData("report", reportNode)
                .build();

        outputs.add(finalOutput);
    }
}
