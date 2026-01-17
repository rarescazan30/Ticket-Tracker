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

/**
 * * Command to generate a comprehensive stability report of the application
 * Aggregates risk and impact scores across all open tickets
 * */
public final class AppStabilityReportCommand extends BaseCommand {

    private static final double ROUNDING_FACTOR = 100.0;
    private static final double IMPACT_THRESHOLD = 50.0;
    private static final double RISK_NEGLIGIBLE_THRESHOLD = 25;
    private static final double RISK_MODERATE_THRESHOLD = 50;
    private static final double RISK_SIGNIFICANT_THRESHOLD = 75;

    /**
     * * Returns the allowed roles for this command
     * Only Managers can view the stability report
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.MANAGER);
    }

    /**
     * * Constructs the stability report command
     * */
    public AppStabilityReportCommand(final List<ObjectNode> outputs, final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the logic to calculate stability metrics
     * Iterates through tickets, applies visitors, and determines overall app stability
     * */
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
                    openTicketsByPriority.put(prio, openTicketsByPriority.get(prio) + 1);
                }

                String type = t.getType();
                if (openTicketsByType.containsKey(type)) {
                    openTicketsByType.put(type, openTicketsByType.get(type) + 1);
                }

                if (riskScoresByType.containsKey(type)) {
                    riskScoresByType.get(type).add(t.accept(riskVisitor));
                }

                if (impactScoresByType.containsKey(type)) {
                    impactScoresByType.get(type).add(t.accept(impactVisitor));
                }
            }
        }

        Map<String, String> riskLabels = calculateRiskLabels(riskScoresByType);
        Map<String, Double> impactAverages = calculateImpactAverages(impactScoresByType);
        String stability = determineStabilityStatus(totalOpenTickets, riskLabels, impactAverages);

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

    /**
     * * Calculates risk labels based on average risk scores
     * */
    private Map<String, String> calculateRiskLabels(
            final Map<String, List<Double>> riskScoresByType) {
        Map<String, String> riskLabels = new LinkedHashMap<>();
        for (String type : riskScoresByType.keySet()) {
            List<Double> scores = riskScoresByType.get(type);
            if (scores.isEmpty()) {
                riskLabels.put(type, "NEGLIGIBLE");
            } else {
                double sum = 0;
                for (Double val : scores) {
                    sum += val;
                }
                double avg = sum / scores.size();
                riskLabels.put(type, getRiskLabel(avg));
            }
        }
        return riskLabels;
    }

    /**
     * * Calculates average impact scores and rounds them
     * */
    private Map<String, Double> calculateImpactAverages(
            final Map<String, List<Double>> impactScoresByType) {
        Map<String, Double> impactAverages = new LinkedHashMap<>();
        for (String type : impactScoresByType.keySet()) {
            List<Double> scores = impactScoresByType.get(type);
            if (scores.isEmpty()) {
                impactAverages.put(type, 0.0);
            } else {
                double sum = 0;
                for (Double val : scores) {
                    sum += val;
                }
                double avg = sum / scores.size();
                impactAverages.put(type, Math.round(avg * ROUNDING_FACTOR) / ROUNDING_FACTOR);
            }
        }
        return impactAverages;
    }

    /**
     * * Determines the overall stability status of the application
     * Checks open tickets, risk levels, and impact thresholds
     * */
    private String determineStabilityStatus(final int totalOpenTickets,
                                            final Map<String, String> riskLabels,
                                            final Map<String, Double> impactAverages) {
        if (totalOpenTickets == 0) {
            return "STABLE";
        }

        boolean allRisksNegligible = true;
        for (String label : riskLabels.values()) {
            if (!"NEGLIGIBLE".equals(label)) {
                allRisksNegligible = false;
                break;
            }
        }

        boolean allImpactsLow = true;
        for (Double val : impactAverages.values()) {
            if (val >= IMPACT_THRESHOLD) {
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
            return "STABLE";
        } else if (anyRiskSignificant) {
            return "UNSTABLE";
        }

        return "PARTIALLY STABLE";
    }

    private String getRiskLabel(final double score) {
        if (score < RISK_NEGLIGIBLE_THRESHOLD) {
            return "NEGLIGIBLE";
        }
        if (score < RISK_MODERATE_THRESHOLD) {
            return "MODERATE";
        }
        if (score < RISK_SIGNIFICANT_THRESHOLD) {
            return "SIGNIFICANT";
        }
        return "MAJOR";
    }
}
