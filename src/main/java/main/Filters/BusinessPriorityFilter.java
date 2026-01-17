package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Ticket.Ticket;
import main.Users.User;

/**
 * Filter that checks if a ticket matches a specific business priority
 * This class implements the Strategy design pattern
 */
public final class BusinessPriorityFilter implements Filter<Ticket> {
    /**
     * Returns true if the ticket business priority matches the priority in the filters
     */
    @Override
    public boolean matches(final Ticket ticket, final JsonNode filters, final User user) {
        if (filters.has("businessPriority")) {
            JsonNode priorities = filters.get("businessPriority");
            String priority = priorities.asText();
            return ticket.getBusinessPriority().toString().equals(priority);
        }
        return true;
    }
}
