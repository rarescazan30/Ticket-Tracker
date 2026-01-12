package main.Users;

import main.Enums.ExpertiseAreaType;
import main.Enums.SeniorityType;

public class Developer  extends User {
    private String hireDate;
    private ExpertiseAreaType expertiseArea;
    private SeniorityType seniority;

    public Developer() {
        super();
    }
    public String getHireDate() { return hireDate; }
    public ExpertiseAreaType getExpertiseArea() { return expertiseArea; }
    public SeniorityType getSeniority() { return seniority; }
}
