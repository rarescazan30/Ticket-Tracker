package main.Users;

import com.fasterxml.jackson.databind.JsonNode;
import main.Enums.BusinessPriorityType;
import main.Enums.RoleType;
import main.Visitor.UserVisitor;

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


    // managers dont need performance reports
    @Override
    public double accept(UserVisitor visitor) {
        return 0;
    }

    public String getHireDate() { return hireDate; }
    public List<String> getSubordinates() { return subordinates; }

    @Override
    public boolean checkIfOverwhelmed(BusinessPriorityType businessPriority) {
        return false;
    }
}
