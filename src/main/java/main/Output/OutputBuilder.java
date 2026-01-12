package main.Output;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Enums.RoleType;

import java.time.LocalDate;

public class OutputBuilder {
    private final ObjectMapper mapper;
    private final ObjectNode node;
    public OutputBuilder(ObjectMapper mapper) {
        this.mapper = mapper;
        this.node = mapper.createObjectNode();
    }
    public OutputBuilder setCommand(String command) {
        this.node.put("command", command);
        return this;
    }
    public OutputBuilder setUser(String username) {
        this.node.put("username", username);
        return this;
    }
    public OutputBuilder setTimestamp(LocalDate timestamp) {
        this.node.put("timestamp", timestamp.toString());
        return this;
    }
    public OutputBuilder addData(String key, Object objectToSerialize) {
        // json looks for getters and automatically builds output
        this.node.set(key, mapper.valueToTree(objectToSerialize));
        return this;
    }

    public ObjectNode build() {
        return this.node;
    }
}
