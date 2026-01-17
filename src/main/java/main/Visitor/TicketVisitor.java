package main.Visitor;

import main.Ticket.Bug;
import main.Ticket.FeatureRequest;
import main.Ticket.UIFeedback;

/**
 * Interface defining the Visitor component for the Ticket hierarchy
 * Allows adding new operations to ticket types without modifying their structure
 */
public interface TicketVisitor {
    /**
     * Operation to be performed on a Bug ticket
     */
    double visit(Bug bug);

    /**
     * Operation to be performed on a FeatureRequest ticket
     */
    double visit(FeatureRequest featureRequest);

    /**
     * Operation to be performed on a UIFeedback ticket
     */
    double visit(UIFeedback uiFeedback);
}
