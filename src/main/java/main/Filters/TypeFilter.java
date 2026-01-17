package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Ticket.Ticket;
import main.Users.User;

/**
 * Filter that checks if a ticket matches a specific type
 * This class implements the Strategy design pattern
 */
public final class TypeFilter implements Filter<Ticket> {
    /**
     * Returns true if the ticket type matches the specified filter type
     */
    @Override
    public boolean matches(final Ticket ticket, final JsonNode filters, final User user) {
        if (filters.has("type")) {
            String type = filters.get("type").asText();
            return ticket.getType().toString().equals(type);
        }
        return true;
    }
}
