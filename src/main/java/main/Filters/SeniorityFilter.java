package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Users.Developer;
import main.Users.User;

/**
 * Filter that checks if a developer matches a specific seniority level
 */
public final class SeniorityFilter implements Filter<User> {
    /**
     * Returns true if the user's seniority matches the level specified in the filters
     */
    @Override
    public boolean matches(final User entity, final JsonNode filters, final User searcher) {
        if (filters.has("seniority")) {
            Developer devTarget = (Developer) entity;
            String requiredSeniority = filters.get("seniority").asText();

            return devTarget.getSeniority().toString().equals(requiredSeniority);
        }
        return true;
    }
}
