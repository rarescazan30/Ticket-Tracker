package main.Ticket;

import com.fasterxml.jackson.databind.JsonNode;
import main.Enums.BusinessValueType;
import main.Enums.CustomerDemandType;

public class FeatureRequest extends Ticket {
    private BusinessValueType businessValue;
    private CustomerDemandType customerDemand;

    public FeatureRequest(int id, String username, JsonNode ticketDetails, String timestamp) {
        super(id, username, ticketDetails, timestamp);
        this.businessValue = BusinessValueType.valueOf(ticketDetails.get("businessValue").asText());
        this.customerDemand = CustomerDemandType.valueOf(ticketDetails.get("customerDemand").asText());
    }
}
