package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Users.Developer;
import main.Users.User;

public class PerformanceScoreBelowFilter implements Filter<User> {
    @Override
    public boolean matches(User entity, JsonNode filters, User searcher) {
        if (!(entity instanceof Developer)) return false;

        if (filters.has("performanceScoreBelow")) {
            Developer devTarget = (Developer) entity;
            double threshold = filters.get("performanceScoreBelow").asDouble();

            // TODO implement function
            // return devTarget.getPerformanceScore() < threshold;
            return false;
        }
        return true;
    }
}