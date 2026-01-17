package main.Enums;

import com.fasterxml.jackson.databind.JsonNode;
import main.Ticket.Bug;
import main.Ticket.FeatureRequest;
import main.Ticket.Ticket;
import main.Ticket.UIFeedback;

/**
 * Enumeration of ticket types that implements the Factory Method design pattern
 */
// to avoid switch statements and simply code, we use this, like a factory
public enum TicketType {
    BUG {
        @Override
        public Ticket createInstance(final int id, final String username,
                                     final JsonNode params, final String timestamp) {
            return new Bug(id, username, params, timestamp);
        }
    },
    FEATURE_REQUEST {
        @Override
        public Ticket createInstance(final int id, final String username,
                                     final JsonNode params, final String timestamp) {
            return new FeatureRequest(id, username, params, timestamp);
        }
    },
    UI_FEEDBACK {
        @Override
        public Ticket createInstance(final int id, final String username,
                                     final JsonNode params, final String timestamp) {
            return new UIFeedback(id, username, params, timestamp);
        }
    };

    /**
     * Creates a new instance of a ticket based on the specific type
     */
    public abstract Ticket createInstance(int id, String username,
                                          JsonNode params, String timestamp);
}
