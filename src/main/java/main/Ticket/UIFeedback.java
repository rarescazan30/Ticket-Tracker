package main.Ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import main.Enums.BusinessValueType;
import main.Visitor.TicketVisitor;

/**
 * * Represents a ticket of the type user feedback
 * Includes usability scores and visual fix suggestions
 * */
public final class UIFeedback extends Ticket {
    private String uiElementId;
    private BusinessValueType businessValue;
    private int usabilityScore; // int (1-10)
    private String screenshotUrl;
    private String suggestedFix;

    /**
     * * Constructs a UIFeedback ticket from JSON details
     * @param id the unique identifier
     * @param username the reporter's username
     * @param ticketDetails JSON node containing feedback specifics
     * @param timestamp the creation time
     * */
    public UIFeedback(final int id, final String username,
                      final JsonNode ticketDetails, final String timestamp) {
        super(id, username, ticketDetails, timestamp);
        if (ticketDetails.has("uiElementId")) {
            this.uiElementId = ticketDetails.get("uiElementId").asText();
        }
        this.businessValue = BusinessValueType.valueOf(BusinessValueType.class,
                ticketDetails.get("businessValue").asText());
        this.usabilityScore = ticketDetails.get("usabilityScore").asInt();
        if (ticketDetails.has("screenshotUrl")) {
            this.screenshotUrl = ticketDetails.get("screenshotUrl").asText();
        }
        if (ticketDetails.has("suggestedFix")) {
            this.suggestedFix = ticketDetails.get("suggestedFix").asText();
        }
    }

    /**
     * * Accepts a visitor for processing this ticket type
     * */
    @Override
    public double accept(final TicketVisitor visitor) {
        return visitor.visit(this);
    }

    /**
     * * Returns the usability score
     * */
    @JsonIgnore
    public int getUsabilityScore() {
        return usabilityScore;
    }

    /**
     * * Returns the business value associated with the feedback
     * */
    @JsonIgnore
    public BusinessValueType getBusinessValue() {
        return businessValue;
    }
}
