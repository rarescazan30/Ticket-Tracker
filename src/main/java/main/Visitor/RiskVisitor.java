package main.Visitor;

import main.Ticket.Bug;
import main.Ticket.FeatureRequest;
import main.Ticket.UIFeedback;
import main.Visitor.TicketVisitor;

public class RiskVisitor implements TicketVisitor {
    @Override
    public double visit(Bug bug) {
        double raw = bug.getFrequency().getValue() * bug.getSeverity().getValue();
        return (raw * 100.0) / 12.0;
    }

    @Override
    public double visit(FeatureRequest fr) {
        double raw = fr.getBusinessValue().getValue() + fr.getCustomerDemand().getValue();
        return (raw * 100.0) / 20.0;
    }

    @Override
    public double visit(UIFeedback ui) {
        double raw = (11 - ui.getUsabilityScore()) * ui.getBusinessValue().getValue();
        return (raw * 100.0) / 100.0;
    }
}