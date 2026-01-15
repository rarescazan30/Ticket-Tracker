package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Ticket.Ticket;
import main.Users.User;

public class BusinessPriorityFilter implements Filter<Ticket> {
    @Override
    public boolean matches(Ticket ticket, JsonNode filters, User user) {
        if (filters.has("businessPriority")) {
            JsonNode priorities = filters.get("businessPriority");
            String priority = priorities.asText();
            return ticket.getBusinessPriority().toString().equals(priority);
        }
        return true;
    }
}
