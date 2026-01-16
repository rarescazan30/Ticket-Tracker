package main.Ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import main.Enums.BusinessValueType;
import main.Enums.CustomerDemandType;
import main.Visitor.TicketVisitor;

public class UIFeedback extends Ticket {
    private String uiElementId;
    private BusinessValueType businessValue;
    private int usabilityScore; // int (1-10)
    private String screenshotUrl;
    private String suggestedFix;

    public UIFeedback(int id, String username, JsonNode ticketDetails, String timestamp) {
        super(id, username, ticketDetails, timestamp);
        if (ticketDetails.has("uiElementId")) {
            this.uiElementId = ticketDetails.get("uiElementId").asText();
        }
        this.businessValue = BusinessValueType.valueOf(BusinessValueType.class, ticketDetails.get("businessValue").asText());
        this.usabilityScore = ticketDetails.get("usabilityScore").asInt();
        if (ticketDetails.has("screenshotUrl")) {
            this.screenshotUrl = ticketDetails.get("screenshotUrl").asText();
        }
        if (ticketDetails.has("suggestedFix")) {
            this.suggestedFix = ticketDetails.get("suggestedFix").asText();
        }
    }
    @Override
    public double accept(TicketVisitor visitor) {
        return visitor.visit(this);
    }
    @JsonIgnore
    public int getUsabilityScore() {
        return usabilityScore;
    }
    @JsonIgnore
    public BusinessValueType getBusinessValue() {
        return businessValue;
    }
}
