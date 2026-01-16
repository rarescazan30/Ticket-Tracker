package main.Ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import main.Enums.BusinessValueType;
import main.Enums.CustomerDemandType;
import main.Visitor.TicketVisitor;

public class FeatureRequest extends Ticket {
    private BusinessValueType businessValue;
    private CustomerDemandType customerDemand;

    public FeatureRequest(int id, String username, JsonNode ticketDetails, String timestamp) {
        super(id, username, ticketDetails, timestamp);
        this.businessValue = BusinessValueType.valueOf(ticketDetails.get("businessValue").asText());
        this.customerDemand = CustomerDemandType.valueOf(ticketDetails.get("customerDemand").asText());
    }
    @Override
    public double accept(TicketVisitor visitor) {
        return visitor.visit(this);
    }
    @JsonIgnore
    public BusinessValueType getBusinessValue() {
        return businessValue;
    }
    @JsonIgnore
    public CustomerDemandType getCustomerDemand() {
        return this.customerDemand;
    }
}
