package main.Visitor;

import main.Ticket.Bug;
import main.Ticket.FeatureRequest;
import main.Ticket.UIFeedback;
import main.Ticket.Ticket;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;

/**
 * Visitor implementation that calculates the resolution efficiency for different ticket types
 * Part of the Visitor design pattern used for ticket metric calculations
 */
public final class EfficiencyVisitor implements TicketVisitor {
    private static final double PERCENT_MULTIPLIER = 100.0;
    private static final double BUG_CONSTANT_WEIGHT = 10.0;
    private static final double BUG_MAX_EFFICIENCY = 70.0;
    private static final double FEATURE_MAX_EFFICIENCY = 20.0;
    private static final double UI_MAX_EFFICIENCY = 20.0;

    /**
     * Calculates the number of days taken to resolve a ticket
     */
    private double getDaysToResolve(final Ticket t) {
        LocalDate start = LocalDate.parse(t.getAssignedAt().toString());
        if (t.getSolvedAt().toString().isEmpty()) {
            return 1.0;
        }
        LocalDate end = LocalDate.parse(t.getSolvedAt().toString());

        long days = ChronoUnit.DAYS.between(start, end) + 1;
        return (double) days;
    }

    @Override
    public double visit(final Bug bug) {
        double days = getDaysToResolve(bug);
        double metrics = bug.getFrequency().getValue() + bug.getSeverity().getValue();
        double score = (metrics * BUG_CONSTANT_WEIGHT) / days;
        return (score * PERCENT_MULTIPLIER) / BUG_MAX_EFFICIENCY;
    }

    @Override
    public double visit(final FeatureRequest fr) {
        double days = getDaysToResolve(fr);
        double metrics = fr.getBusinessValue().getValue() + fr.getCustomerDemand().getValue();
        double score = metrics / days;
        return (score * PERCENT_MULTIPLIER) / FEATURE_MAX_EFFICIENCY;
    }

    @Override
    public double visit(final UIFeedback ui) {
        double days = getDaysToResolve(ui);
        double metrics = ui.getUsabilityScore() + ui.getBusinessValue().getValue();
        double score = metrics / days;
        return (score * PERCENT_MULTIPLIER) / UI_MAX_EFFICIENCY;
    }
}
