package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Ticket.Ticket;
import main.Users.User;

/**
 * Filter that checks if a ticket title or description contains specific keywords
 * This class implements the Strategy design pattern
 */
public final class KeywordsFilter implements Filter<Ticket> {
    /**
     * Returns true if the ticket contains any of the specified keywords
     */
    @Override
    public boolean matches(final Ticket ticket, final JsonNode filters, final User user) {
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
