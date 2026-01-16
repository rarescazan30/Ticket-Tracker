package main.Visitor;

import main.Users.Developer;
import main.Users.Manager;

public interface UserVisitor {
    double visit(Developer developer);
}
