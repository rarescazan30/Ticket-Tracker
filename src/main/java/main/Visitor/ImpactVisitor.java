package main.Visitor;

import main.Ticket.Bug;
import main.Ticket.FeatureRequest;
import main.Ticket.UIFeedback;

/**
 * Visitor implementation that calculates the impact score for different ticket types
 * Implements the Visitor design pattern
 */
public final class ImpactVisitor implements TicketVisitor {
    private static final double PERCENT_MULTIPLIER = 100.0;
    private static final double BUG_MAX_SCORE = 48.0;
    private static final double FEATURE_MAX_SCORE = 100.0;
    private static final double UI_MAX_SCORE = 100.0;

    @Override
    public double visit(final Bug bug) {
        double frequency = bug.getFrequency().getValue();
        double priority = bug.getBusinessPriority().getScore();
        double severity = bug.getSeverity().getValue();

        double raw = frequency * priority * severity;
        return (raw * PERCENT_MULTIPLIER) / BUG_MAX_SCORE;
    }

    @Override
    public double visit(final FeatureRequest fr) {
        double raw = fr.getBusinessValue().getValue() * fr.getCustomerDemand().getValue();
        return (raw * PERCENT_MULTIPLIER) / FEATURE_MAX_SCORE;
    }

    @Override
    public double visit(final UIFeedback ui) {
        double raw = ui.getBusinessValue().getValue() * ui.getUsabilityScore();
        return (raw * PERCENT_MULTIPLIER) / UI_MAX_SCORE;
    }
}
