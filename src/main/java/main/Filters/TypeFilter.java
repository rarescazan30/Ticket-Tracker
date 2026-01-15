package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Filters.Filter;
import main.Ticket.Ticket;
import main.Users.User;

public class TypeFilter implements Filter<Ticket> {
    @Override
    public boolean matches(Ticket ticket, JsonNode filters, User user) {
        if (filters.has("type")) {
            String type = filters.get("type").asText();
            return ticket.getType().toString().equals(type);
        }
        return true;
    }
}