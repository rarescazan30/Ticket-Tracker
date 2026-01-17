package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Ticket.Ticket;
import main.Users.User;

/**
 * Filter that checks if a ticket was created on a specific date
 */
public final class CreatedAtFilter implements Filter<Ticket> {
    /**
     * Returns true if the ticket creation date matches the date specified in the filters
     */
    @Override
    public boolean matches(final Ticket ticket, final JsonNode filters, final User user) {
        if (filters.has("createdAt")) {
            String date = filters.get("createdAt").asText();
            return ticket.getCreatedAt().equals(date);
        }
        return true;
    }
}
