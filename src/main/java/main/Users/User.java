package main.Users;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import main.Enums.BusinessPriorityType;
import main.Enums.RoleType;
import main.Visitor.UserVisitor;

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

    public User(){
        // empty constructor for Jackson
    }

    public User(String username, String email, RoleType role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }

    public RoleType getRole() {
        return role;
    }
    public abstract double accept(UserVisitor visitor);

    public abstract boolean checkIfOverwhelmed(BusinessPriorityType businessPriority); {}
}
