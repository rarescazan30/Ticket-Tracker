package main.Visitor;

import main.Ticket.Bug;
import main.Ticket.FeatureRequest;
import main.Ticket.UIFeedback;

/**
 * Visitor implementation that calculates risk scores for different ticket types
 * Part of the Visitor design pattern used for risk assessment
 */
public final class RiskVisitor implements TicketVisitor {
    private static final double PERCENT_MULTIPLIER = 100.0;
    private static final double BUG_MAX_RISK = 12.0;
    private static final double FEATURE_MAX_RISK = 20.0;
    private static final double UI_MAX_RISK = 100.0;
    private static final int UI_USABILITY_OFFSET = 11;

    @Override
    public double visit(final Bug bug) {
        double raw = bug.getFrequency().getValue() * bug.getSeverity().getValue();
        return (raw * PERCENT_MULTIPLIER) / BUG_MAX_RISK;
    }

    @Override
    public double visit(final FeatureRequest fr) {
        double raw = fr.getBusinessValue().getValue() + fr.getCustomerDemand().getValue();
        return (raw * PERCENT_MULTIPLIER) / FEATURE_MAX_RISK;
    }

    @Override
    public double visit(final UIFeedback ui) {
        double raw = (UI_USABILITY_OFFSET - ui.getUsabilityScore())
                      * ui.getBusinessValue().getValue();
        return (raw * PERCENT_MULTIPLIER) / UI_MAX_RISK;
    }
}
