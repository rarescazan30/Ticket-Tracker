package main.Ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import main.Enums.BusinessValueType;
import main.Enums.FrequencyType;
import main.Enums.SeverityType;
import main.Visitor.TicketVisitor;

public class Bug extends Ticket {
    private String expectedBehavior;
    private String actualBehavior;
    private FrequencyType frequency;
    private SeverityType severity;
    private String environment;
    private int errorCode;
    public Bug(int id, String username,  JsonNode ticketDetails, String timestamp) {
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
    @Override
    public double accept(TicketVisitor visitor) {
        return visitor.visit(this);
    }
    @JsonIgnore
    public FrequencyType getFrequency() {
        return this.frequency;
    }
    @JsonIgnore
    public SeverityType getSeverity() {
        return this.severity;
    }
}
