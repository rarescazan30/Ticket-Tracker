package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Filters.Filter;
import main.Ticket.Ticket;
import main.Users.User;

public class CreatedAtFilter implements Filter<Ticket> {
    @Override
    public boolean matches(Ticket ticket, JsonNode filters, User user) {
        if (filters.has("createdAt")) {
            String date = filters.get("createdAt").asText();
            return ticket.getCreatedAt().equals(date);
        }
        return true;
    }
}