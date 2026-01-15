package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Users.Developer;
import main.Users.User;

public class SeniorityFilter implements Filter<User> {
    @Override
    public boolean matches(User entity, JsonNode filters, User searcher) {
        if (filters.has("seniority")) {
            Developer devTarget = (Developer) entity;
            String requiredSeniority = filters.get("seniority").asText();

            return devTarget.getSeniority().toString().equals(requiredSeniority);
        }
        return true;
    }
}