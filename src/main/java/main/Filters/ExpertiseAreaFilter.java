package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Users.Developer;
import main.Users.User;

/**
 * Filter that checks if a developer matches a specific expertise area
 * This class implements the Strategy design pattern
 */
public final class ExpertiseAreaFilter implements Filter<User> {
    /**
     * Returns true if the user expertise matches the required expertise
     */
    @Override
    public boolean matches(final User entity, final JsonNode filters, final User searcher) {
        if (filters.has("expertiseArea")) {
            Developer devTarget = (Developer) entity;
            String requiredExpertise = filters.get("expertiseArea").asText();

            return devTarget.getExpertiseArea().toString().equals(requiredExpertise);
        }
        return true;
    }
}
