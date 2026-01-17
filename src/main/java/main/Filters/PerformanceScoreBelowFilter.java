package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Users.Developer;
import main.Users.User;

/**
 * Filter that checks if a developer's performance score is below a certain threshold
 */
public final class PerformanceScoreBelowFilter implements Filter<User> {
    /**
     * Returns true if the developer's performance score is less than or equal to the threshold
     */
    @Override
    public boolean matches(final User entity, final JsonNode filters, final User searcher) {
        if (!(entity instanceof Developer)) {
            return false;
        }

        if (filters.has("performanceScoreBelow")) {
            Developer devTarget = (Developer) entity;
            double threshold = filters.get("performanceScoreBelow").asDouble();

            return devTarget.getPerformanceScore() <= threshold;
        }
        return true;
    }
}
