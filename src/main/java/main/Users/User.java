package main.Users;

import main.Enums.RoleType;

public class User {
    private String username;
    private String email;
    private RoleType role;

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
}
