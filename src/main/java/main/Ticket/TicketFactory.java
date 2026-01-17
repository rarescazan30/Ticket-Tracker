package main.Ticket;

import com.fasterxml.jackson.databind.JsonNode;
import main.Enums.TicketType;

/**
 * Factory class responsible for instantiating different types of Ticket objects
 * Implements the Static Factory Method design pattern
 */
public final class TicketFactory {

    private TicketFactory() {
        // private constructor to prevent instantiation
    }

    /**
     * Creates a concrete Ticket instance based on the provided JSON command details
     * @param id the unique identifier to be assigned to the new ticket
     * @param command the JSON node containing user, timestamp and ticket parameters
     */
    public static Ticket create(final int id, final JsonNode command) {
        String username = command.get("username").asText();
        JsonNode params = command.get("params");
        String timestamp = command.get("timestamp").asText();
        String type = params.get("type").asText();
        TicketType ticketType = TicketType.valueOf(type);
        return ticketType.createInstance(id, username, params, timestamp);
    }
}
