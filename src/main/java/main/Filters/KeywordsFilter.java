package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Enums.RoleType;
import main.Filters.Filter;
import main.Ticket.Ticket;
import main.Users.User;

public class KeywordsFilter implements Filter<Ticket> {
    @Override
    public boolean matches(Ticket ticket, JsonNode filters, User user) {
        if (filters.has("keywords")) {
            String content = (ticket.getTitle() + " " + ticket.getDescription()).toLowerCase();

            for (JsonNode node : filters.get("keywords")) {
                String keyword = node.asText().toLowerCase();
                if (content.contains(keyword)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}