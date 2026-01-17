package main.Users;

import main.Enums.BusinessPriorityType;
import main.Visitor.UserVisitor;

public class Reporter extends User {
    public Reporter() {
        super();
    }

    @Override
    public double accept(UserVisitor visitor) {
        return 0; // reporter doesnt get visited for performance score
    }
    @Override
    public boolean checkIfOverwhelmed(BusinessPriorityType businessPriority) {
        return false;
    }
}
