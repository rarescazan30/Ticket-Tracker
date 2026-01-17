package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Users.Developer;
import main.Users.User;

/**
 * Filter that checks if a developer performance score is above a threshold
 * This class implements the Strategy design pattern
 */
public final class PerformanceScoreAboveFilter implements Filter<User> {
    /**
     * Returns true if the developer performance score is greater than or equal to the threshold
     */
    @Override
    public boolean matches(final User entity, final JsonNode filters, final User searcher) {
        if (!(entity instanceof Developer)) {
            return false;
        }

        if (filters.has("performanceScoreAbove")) {
            Developer devTarget = (Developer) entity;
            double threshold = filters.get("performanceScoreAbove").asDouble();

            return devTarget.getPerformanceScore() >= threshold;
        }
        return true;
    }
}
