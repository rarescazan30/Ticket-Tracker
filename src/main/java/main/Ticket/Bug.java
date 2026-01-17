package main.Ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import main.Enums.FrequencyType;
import main.Enums.SeverityType;
import main.Visitor.TicketVisitor;

/**
 * * Represents a bug
 * Contains details about expected vs actual behavior and severity
 * */
public final class Bug extends Ticket {
    private String expectedBehavior;
    private String actualBehavior;
    private FrequencyType frequency;
    private SeverityType severity;
    private String environment;
    private int errorCode;

    /**
     * * Constructs a new Bug ticket from the provided JSON details
     * @param id the unique identifier
     * @param username the reporter's username
     * @param ticketDetails JSON node containing bug specifics
     * @param timestamp the creation time
     * */
    public Bug(final int id, final String username,
               final JsonNode ticketDetails, final String timestamp) {
        super(id, username, ticketDetails, timestamp);
        this.expectedBehavior = ticketDetails.get("expectedBehavior").asText();
        this.actualBehavior = ticketDetails.get("actualBehavior").asText();
        this.frequency = FrequencyType.valueOf(ticketDetails.get("frequency").asText());
        this.severity = SeverityType.valueOf(ticketDetails.get("severity").asText());
        if (ticketDetails.get("environment") != null) {
            this.environment = ticketDetails.get("environment").asText();
        }
        if (ticketDetails.get("errorCode") != null) {
            this.errorCode = ticketDetails.get("errorCode").asInt();
        }
    }

    /**
     * * Accepts a visitor to perform operations on this bug ticket
     * Part of the Visitor design pattern
     * */
    @Override
    public double accept(final TicketVisitor visitor) {
        return visitor.visit(this);
    }

    /**
     * * Returns the frequency of occurrence for this bug
     * */
    @JsonIgnore
    public FrequencyType getFrequency() {
        return this.frequency;
    }

    /**
     * * Returns the severity level of this bug
     * */
    @JsonIgnore
    public SeverityType getSeverity() {
        return this.severity;
    }
}
