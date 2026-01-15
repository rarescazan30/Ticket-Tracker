package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Users.Developer;
import main.Users.User;

public class ExpertiseAreaFilter implements Filter<User> {
    @Override
    public boolean matches(User entity, JsonNode filters, User searcher) {
        if (filters.has("expertiseArea")) {
            Developer devTarget = (Developer) entity;
            String requiredExpertise = filters.get("expertiseArea").asText();

            return devTarget.getExpertiseArea().toString().equals(requiredExpertise);
        }
        return true;
    }
}