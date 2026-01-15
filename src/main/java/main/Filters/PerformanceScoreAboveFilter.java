package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Users.Developer;
import main.Users.User;

public class PerformanceScoreAboveFilter implements Filter<User> {
    @Override
    public boolean matches(User entity, JsonNode filters, User searcher) {
        if (!(entity instanceof Developer)) return false;

        if (filters.has("performanceScoreAbove")) {
            Developer devTarget = (Developer) entity;
            double threshold = filters.get("performanceScoreAbove").asDouble();

            // return devTarget.getPerformanceScore() > threshold;\
            // TODO: implement devTarget.getPerformanceScore
            return false;
        }
        return true;
    }
}