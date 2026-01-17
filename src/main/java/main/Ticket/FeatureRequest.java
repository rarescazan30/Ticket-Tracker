package main.Ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import main.Enums.BusinessValueType;
import main.Enums.CustomerDemandType;
import main.Visitor.TicketVisitor;

/**
 * Represents a ticket for requesting new system features
 */
public final class FeatureRequest extends Ticket {
    private BusinessValueType businessValue;
    private CustomerDemandType customerDemand;

    /**
     * Constructs a FeatureRequest from JSON ticket details
     * @param id unique identifier for the ticket
     * @param username the reporter's username
     * @param ticketDetails JSON node containing feature specific data
     * @param timestamp the creation time
     */
    public FeatureRequest(final int id, final String username,
                          final JsonNode ticketDetails, final String timestamp) {
        super(id, username, ticketDetails, timestamp);
        this.businessValue = BusinessValueType.valueOf(ticketDetails.get("businessValue").asText());
        this.customerDemand = CustomerDemandType
                .valueOf(ticketDetails.get("customerDemand").asText());
    }

    /**
     * Acceptance method for the TicketVisitor
     */
    @Override
    public double accept(final TicketVisitor visitor) {
        return visitor.visit(this);
    }

    /**
     * Returns the business value associated with the feature
     */
    @JsonIgnore
    public BusinessValueType getBusinessValue() {
        return businessValue;
    }

    /**
     * Returns the customer demand level for the feature
     */
    @JsonIgnore
    public CustomerDemandType getCustomerDemand() {
        return this.customerDemand;
    }
}
