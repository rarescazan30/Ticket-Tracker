package main.Output;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDate;

/**
 * Utility class that implements the Builder design pattern for JSON output generation
 * Uses Jackson's ObjectMapper to transform Java objects into JSON nodes by
 * looking for public getters and annotations to map fields to JSON keys
 */
public final class OutputBuilder {
    private final ObjectMapper mapper;
    private final ObjectNode node;

    /**
     * Constructs an OutputBuilder with a Jackson ObjectMapper
     */
    public OutputBuilder(final ObjectMapper mapper) {
        this.mapper = mapper;
        this.node = mapper.createObjectNode();
    }

    /**
     * Sets the command name in the output node
     */
    public OutputBuilder setCommand(final String command) {
        this.node.put("command", command);
        return this;
    }

    /**
     * Sets the username in the output node
     */
    public OutputBuilder setUser(final String username) {
        this.node.put("username", username);
        return this;
    }

    /**
     * Sets the timestamp in the output node
     */
    public OutputBuilder setTimestamp(final LocalDate timestamp) {
        this.node.put("timestamp", timestamp.toString());
        return this;
    }

    /**
     * Adds a serialized object to the output node under a specific key
     * @param key the JSON key for the object
     * @param objectToSerialize the object to be converted to JSON
     */
    public OutputBuilder addData(final String key, final Object objectToSerialize) {
        // json looks for getters and automatically builds output
        this.node.set(key, mapper.valueToTree(objectToSerialize));
        return this;
    }

    /**
     * Adds a simple string key-value pair to the output node
     * @param key the JSON key
     * @param value the string value
     */
    public OutputBuilder put(final String key, final String value) {
        this.node.put(key, value);
        return this;
    }

    /**
     * Returns the constructed ObjectNode
     */
    public ObjectNode build() {
        return this.node;
    }
}
