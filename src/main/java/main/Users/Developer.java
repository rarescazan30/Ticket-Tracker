package main.Users;

import main.Enums.BusinessPriorityType;
import main.Enums.ExpertiseAreaType;
import main.Enums.SeniorityType;
import main.Visitor.UserVisitor;

import java.util.ArrayList;
import java.util.List;

public class Developer  extends User {
    private String hireDate;
    private ExpertiseAreaType expertiseArea;
    private SeniorityType seniority;
    private List<String> notifications = new ArrayList<>();
    private double performanceScore;

    public Developer() {
        super();
    }
    public String getHireDate() { return hireDate; }
    public ExpertiseAreaType getExpertiseArea() { return expertiseArea; }
    public SeniorityType getSeniority() { return seniority; }

    public double getPerformanceScore() {
        return this.performanceScore;
    }
    public void addNotification(String notification) {
        this.notifications.add(notification);
    }
    public List<String> getNotificationsAndClear() {
        List<String> currentNotifications = new ArrayList<>(this.notifications);
        this.notifications.clear(); // we delete once we use it (we use it to print it)
        return currentNotifications;
    }
    public double accept(UserVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean checkIfOverwhelmed(BusinessPriorityType businessPriority) {
        boolean overwhelmed = false;
        if (businessPriority == BusinessPriorityType.CRITICAL) {
            if (getSeniority() != SeniorityType.SENIOR) {
                overwhelmed = true;
            }
        } else if  (businessPriority == BusinessPriorityType.HIGH) {
            if (getSeniority() != SeniorityType.SENIOR) {
                overwhelmed = true;
            }
        }
        return overwhelmed;
    }

    public void setPerformanceScore(double score) {
        this.performanceScore = score;
    }
}
