package main.Ticket;

import com.fasterxml.jackson.databind.JsonNode;

public class TicketFactory {
    public static Ticket create(int id, JsonNode command) {
        String username = command.get("username").asText();
        JsonNode params  = command.get("params");
        String timestamp = command.get("timestamp").asText();
        String type =  params.get("type").asText();
        switch (type) {
            case "BUG":
                return new Bug(id, username, params, timestamp);
            case "FEATURE_REQUEST":
                return new FeatureRequest(id, username, params, timestamp);
            case "UI_FEEDBACK":
                return new UIFeedback(id, username, params, timestamp);
            default:
                System.out.println("Error Command Unknown");
                return null;
        }
    }
}
