package main.Visitor;

import main.Users.Developer;

/**
 * Interface for the Visitor pattern used to calculate metrics for different user types
 */
public interface UserVisitor {
    /**
     * Visits a developer to calculate performance or other user-specific metrics
     */
    double visit(Developer developer);
}
