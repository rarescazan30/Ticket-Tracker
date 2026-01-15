package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Ticket.Ticket;
import main.Users.User;
import java.time.LocalDate;

public class CreatedBeforeFilter implements Filter<Ticket> {
    @Override
    public boolean matches(Ticket ticket, JsonNode filters, User user) {
        if (filters.has("createdBefore")) {
            LocalDate filterDate = LocalDate.parse(filters.get("createdBefore").asText());
            LocalDate ticketDate = ticket.getCreatedAt();
            return ticketDate.isBefore(filterDate);
        }
        return true;
    }
}