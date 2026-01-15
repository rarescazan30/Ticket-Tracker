package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Filters.Filter;
import main.Ticket.Ticket;
import main.Users.User;
import java.time.LocalDate;

public class CreatedAfterFilter implements Filter<Ticket> {
    @Override
    public boolean matches(Ticket ticket, JsonNode filters, User user) {
        if (filters.has("createdAfter")) {
            LocalDate filterDate = LocalDate.parse(filters.get("createdAfter").asText());
            LocalDate ticketDate = ticket.getCreatedAt();
            return ticketDate.isAfter(filterDate);
        }
        return true;
    }
}