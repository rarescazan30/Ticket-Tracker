package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Ticket.Ticket;
import main.Users.User;
import java.time.LocalDate;

/**
 * Filter that checks if a ticket was created before a specified date
 */
public final class CreatedBeforeFilter implements Filter<Ticket> {
    /**
     * Returns true if the ticket creation date is before the date specified in the filters
     */
    @Override
    public boolean matches(final Ticket ticket, final JsonNode filters, final User user) {
        if (filters.has("createdBefore")) {
            LocalDate filterDate = LocalDate.parse(filters.get("createdBefore").asText());
            LocalDate ticketDate = ticket.getCreatedAt();
            return ticketDate.isBefore(filterDate);
        }
        return true;
    }
}
