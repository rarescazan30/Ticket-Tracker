package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Users.User;

/**
 * Generic interface for filtering entities based on specific criteria
 * This interface defines the contract for the Strategy design pattern
 */
public interface Filter<T> {
    /**
     * Determines if the given entity satisfies the provided filter criteria
     */
    boolean matches(T entity, JsonNode filters, User searcher);
}
