package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Ticket.Ticket;
import main.Users.User;
import java.time.LocalDate;

/**
 * Filter that checks if a ticket was created after a specified date
 * This class implements the Strategy design pattern
 */
public final class CreatedAfterFilter implements Filter<Ticket> {
    /**
     * Returns true if the ticket creation date is after the filter date
     */
    @Override
    public boolean matches(final Ticket ticket, final JsonNode filters, final User user) {
        if (filters.has("createdAfter")) {
            LocalDate filterDate = LocalDate.parse(filters.get("createdAfter").asText());
            LocalDate ticketDate = ticket.getCreatedAt();
            return ticketDate.isAfter(filterDate);
        }
        return true;
    }
}
