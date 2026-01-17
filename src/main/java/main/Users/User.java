package main.Users;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import main.Enums.BusinessPriorityType;
import main.Enums.RoleType;
import main.Milestone.Milestone;
import main.Ticket.Ticket;
import main.Visitor.UserVisitor;

import java.util.List;

/**
 * Base abstract class for all system users
 * Supports polymorphic JSON deserialization through Jackson annotations
 */
// configuration for easier input reading with JSON
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY, // the role exists specifically in my data
        property = "role", // this decides how we handle object
        visible = true // we want to keep the role too
)
// map role to specific subclasses
@JsonSubTypes({
        @JsonSubTypes.Type(value = Manager.class, name = "MANAGER"),
        @JsonSubTypes.Type(value = Developer.class, name = "DEVELOPER"),
        @JsonSubTypes.Type(value = Reporter.class, name = "REPORTER")
})
public abstract class User {
    private String username;
    private String email;
    private RoleType role;

    /**
     * empty constructor for Jackson
     */
    public User() {
        // empty constructor for Jackson
    }

    /**
     * Constructs a user with basic credentials and role
     * @param username the unique identifier of the user
     * @param email the contact email address
     * @param role the user's role type
     */
    public User(final String username, final String email, final RoleType role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    /**
     * Returns the username of the user
     */
    public final String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user
     */
    public final void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Returns the email of the user
     */
    public final String getEmail() {
        return email;
    }

    /**
     * Returns the role of the user
     */
    public final RoleType getRole() {
        return role;
    }

    /**
     * Acceptance method for the Visitor design pattern
     */
    public abstract double accept(UserVisitor visitor);

    /**
     * Checks if the user has reached their workload capacity
     */
    public abstract boolean checkIfOverwhelmed(BusinessPriorityType businessPriority);

    /**
     * * Filters the list of milestones based on the user's role permissions
     * Managers see what they created, Developers see what they are assigned to
     * */
    public abstract List<Milestone> filterVisibleMilestones(List<Milestone> milestones);

    /**
     * * Filters the list of visible tickets on the user's role permissions
     * */
    public abstract List<Ticket> filterVisibleTickets(List<Ticket> allTickets,
                                                      List<Milestone> milestones);
}
