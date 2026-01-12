package main.Ticket;

import com.fasterxml.jackson.databind.JsonNode;
import main.Enums.FrequencyType;
import main.Enums.SeverityType;

public class Bug extends Ticket {
    private String expectedBehaviour;
    private String actualBehaviour;
    private FrequencyType frequency;
    private SeverityType severity;
    private String environment;
    private int errorCode;
    public Bug(int id, String username,  JsonNode ticketDetails) {
        super(id, username, ticketDetails);
        this.expectedBehaviour = ticketDetails.get("expectedBehaviour").asText();
        this.actualBehaviour = ticketDetails.get("actualBehaviour").asText();
        this.frequency = FrequencyType.valueOf(ticketDetails.get("frequency").asText());
        this.severity = SeverityType.valueOf(ticketDetails.get("severity").asText());
        if (ticketDetails.get("environment") != null) {
            this.environment = ticketDetails.get("environment").asText();
        }
        if (ticketDetails.get("errorCode") != null) {
            this.errorCode = ticketDetails.get("errorCode").asInt();
        }
    }
    public String getExpectedBehaviour() { return expectedBehaviour; }
    public String getActualBehaviour() { return actualBehaviour; }
    public FrequencyType getFrequency() { return frequency; }
    public SeverityType getSeverity() { return severity; }
    public String getEnvironment() { return environment; }
    public int getErrorCode() { return errorCode; }
}
