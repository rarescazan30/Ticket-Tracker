package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Exceptions.StopExecutionException;
import main.Output.OutputBuilder;
import main.Ticket.Ticket;
import main.Visitor.ImpactVisitor;
import main.Visitor.RiskVisitor;
import main.Visitor.TicketVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppStabilityReportCommand extends BaseCommand {
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER);
    }

    public AppStabilityReportCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }

    @Override
    public void executeLogic() {
        List<Ticket> tickets = Database.getInstance().getTickets();

        int totalOpenTickets = 0;
        Map<String, Integer> openTicketsByPriority = new LinkedHashMap<>();
        openTicketsByPriority.put("LOW", 0);
        openTicketsByPriority.put("MEDIUM", 0);
        openTicketsByPriority.put("HIGH", 0);
        openTicketsByPriority.put("CRITICAL", 0);

        Map<String, Integer> openTicketsByType = new LinkedHashMap<>();
        openTicketsByType.put("BUG", 0);
        openTicketsByType.put("FEATURE_REQUEST", 0);
        openTicketsByType.put("UI_FEEDBACK", 0);

        Map<String, List<Double>> riskScoresByType = new LinkedHashMap<>();
        riskScoresByType.put("BUG", new ArrayList<>());
        riskScoresByType.put("FEATURE_REQUEST", new ArrayList<>());
        riskScoresByType.put("UI_FEEDBACK", new ArrayList<>());

        Map<String, List<Double>> impactScoresByType = new LinkedHashMap<>();
        impactScoresByType.put("BUG", new ArrayList<>());
        impactScoresByType.put("FEATURE_REQUEST", new ArrayList<>());
        impactScoresByType.put("UI_FEEDBACK", new ArrayList<>());

        TicketVisitor riskVisitor = new RiskVisitor();
        TicketVisitor impactVisitor = new ImpactVisitor();

        for (Ticket t : tickets) {
            if (t.getStatus() == StatusType.OPEN || t.getStatus() == StatusType.IN_PROGRESS) {
                totalOpenTickets++;

                String prio = t.getBusinessPriority().toString();
                if (openTicketsByPriority.containsKey(prio)) {
                    int current = openTicketsByPriority.get(prio);
                    openTicketsByPriority.put(prio, current + 1);
                }

                String type = t.getType();
                if (openTicketsByType.containsKey(type)) {
                    int current = openTicketsByType.get(type);
                    openTicketsByType.put(type, current + 1);
                }

                double riskScore = t.accept(riskVisitor);
                if (riskScoresByType.containsKey(type)) {
                    riskScoresByType.get(type).add(riskScore);
                }

                double impactScore = t.accept(impactVisitor);
                if (impactScoresByType.containsKey(type)) {
                    impactScoresByType.get(type).add(impactScore);
                }
            }
        }

        Map<String, String> riskLabels = new LinkedHashMap<>();
        for (String type : riskScoresByType.keySet()) {
            List<Double> scores = riskScoresByType.get(type);
            if (scores.isEmpty()) {
                riskLabels.put(type, "NEGLIGIBLE");
            } else {
                double sum = 0;
                for (Double val : scores) sum += val;
                double avg = sum / scores.size();
                riskLabels.put(type, getRiskLabel(avg));
            }
        }

        Map<String, Double> impactAverages = new LinkedHashMap<>();
        for (String type : impactScoresByType.keySet()) {
            List<Double> scores = impactScoresByType.get(type);
            if (scores.isEmpty()) {
                impactAverages.put(type, 0.0);
            } else {
                double sum = 0;
                for (Double val : scores) sum += val;
                double avg = sum / scores.size();
                impactAverages.put(type, Math.round(avg * 100.0) / 100.0);
            }
        }

        String stability = "PARTIALLY STABLE";

        boolean noOpenTickets = (totalOpenTickets == 0);
        if (noOpenTickets) {
            stability = "STABLE";
        } else {
            boolean allRisksNegligible = true;
            for (String label : riskLabels.values()) {
                if (!"NEGLIGIBLE".equals(label)) {
                    allRisksNegligible = false;
                    break;
                }
            }

            boolean allImpactsLow = true;
            for (Double val : impactAverages.values()) {
                if (val >= 50.0) {
                    allImpactsLow = false;
                    break;
                }
            }

            boolean anyRiskSignificant = false;
            for (String label : riskLabels.values()) {
                if ("SIGNIFICANT".equals(label)) {
                    anyRiskSignificant = true;
                    break;
                }
            }

            if (allRisksNegligible && allImpactsLow) {
                stability = "STABLE";
            } else if (anyRiskSignificant) {
                stability = "UNSTABLE";
            }
        }

        ObjectNode reportNode = mapper.createObjectNode();
        reportNode.put("totalOpenTickets", totalOpenTickets);
        reportNode.set("openTicketsByType", mapper.valueToTree(openTicketsByType));
        reportNode.set("openTicketsByPriority", mapper.valueToTree(openTicketsByPriority));
        reportNode.set("riskByType", mapper.valueToTree(riskLabels));
        reportNode.set("impactByType", mapper.valueToTree(impactAverages));
        reportNode.put("appStability", stability);

        ObjectNode finalOutput = new OutputBuilder(mapper)
                .setCommand("appStabilityReport")
                .setUser(this.username)
                .setTimestamp(this.timestamp)
                .addData("report", reportNode)
                .build();

        outputs.add(finalOutput);

        if ("STABLE".equals(stability)) {
            throw new StopExecutionException();
        }
    }

    private String getRiskLabel(double score) {
        if (score < 25) return "NEGLIGIBLE";
        if (score < 50) return "MODERATE";
        if (score < 75) return "SIGNIFICANT";
        return "MAJOR";
    }
}