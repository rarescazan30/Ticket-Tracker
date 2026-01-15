package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Users.User;

public interface Filter<T> {
    boolean matches(T entity, JsonNode filters, User searcher);
}
