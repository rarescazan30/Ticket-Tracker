package main.Visitor;

import main.Ticket.Bug;
import main.Ticket.FeatureRequest;
import main.Ticket.UIFeedback;

public interface TicketVisitor {
    double visit(Bug bug);
    double visit(FeatureRequest featureRequest);
    double visit(UIFeedback uiFeedback);
}