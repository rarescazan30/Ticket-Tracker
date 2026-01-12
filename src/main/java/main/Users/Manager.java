package main.Users;

import com.fasterxml.jackson.databind.JsonNode;
import main.Enums.RoleType;

import java.util.ArrayList;
import java.util.List;

public class Manager extends User {
    private String hireDate;
    private List<String> subordinates;
    // now Jackson handles this part automatically
    public Manager() {
        super();
        this.subordinates = new ArrayList<>();
    }

    public String getHireDate() { return hireDate; }
    public List<String> getSubordinates() { return subordinates; }
}
