package main.Ticket;

import com.fasterxml.jackson.databind.JsonNode;

public class TicketFactory {
    public static Ticket create(int id, JsonNode command) {
        String type =  command.get("type").asText();
        String username = command.get("username").asText();
        switch (type) {
            case "BUG":
                return new Bug(id, username, command);
            case "FEATURE_REQUEST":
                return new FeatureRequest(id, username, command);
            case "UI_FEEDBACK":
                return new UIFeedback(id, username, command);
            default:
                System.out.println("Error Command Unknown");
                return null;
        }
    }
}
