package main.Ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import jdk.jshell.Snippet;
import main.Comments.Comment;
import main.Database.Database;
import main.Enums.*;
import main.Exceptions.NonBugAnonymous;
import main.Users.User;
import main.Visitor.TicketVisitor;

import javax.management.relation.Role;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
* */
// we tell Jackson how we want the output
@JsonPropertyOrder({
        "id", "type", "title",
        "businessPriority", "status",
        "createdAt", "assignedAt", "solvedAt",
        "assignedTo", "reportedBy", "comments"
})
public abstract class Ticket {
    private String username;
    private int id;
    private String type;
    private String title;
    private BusinessPriorityType businessPriority;
    private StatusType status;
    private ExpertiseType expertiseArea;
    private String description;
    private String reportedBy;
    private LocalDate createdAt;
    private LocalDate solvedAt;
    private String assignedTo;
    private LocalDate assignedAt;
    private List<Comment> comments;
    private List<TicketAction> actions = new ArrayList<>();

    public Ticket (int id, String username, JsonNode ticketDetails, String timestamp) {
        this.username = username;
        this.id = id;
        this.type = ticketDetails.get("type").asText();
        this.title = ticketDetails.get("title").asText();
        this.businessPriority = BusinessPriorityType.valueOf(ticketDetails.get("businessPriority").asText());
        this.status = StatusType.OPEN;
        this.expertiseArea = ExpertiseType.valueOf(ticketDetails.get("expertiseArea").asText());
        if (ticketDetails.has("description")) {
            this.description = ticketDetails.get("description").asText();
        }
        String reported = ticketDetails.get("reportedBy").asText();
        if  (!reported.isEmpty()) {
            this.reportedBy = reported;
        } else {
            if (this.type.equals("BUG")) {
                this.reportedBy = "ANONIM";
                this.businessPriority = BusinessPriorityType.LOW;
            } else throw new NonBugAnonymous("Anonymous reports are only allowed for tickets of type BUG.");
        }
        this.createdAt = LocalDate.parse(timestamp);
        this.comments = new ArrayList<>();

    }

    public int getId() { return id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public BusinessPriorityType getBusinessPriority() { return businessPriority; }
    public StatusType getStatus() { return status; }
    @JsonIgnore
    public ExpertiseType getExpertiseArea() { return expertiseArea; }
    @JsonIgnore
    public String getDescription() { return description; }
    public Object getSolvedAt() {
        if (solvedAt == null) return "";
        return solvedAt.toString();
    }
    public Object getAssignedAt() {
        if (assignedAt == null) return "";
        return assignedAt.toString();
    }
    public String getAssignedTo() {
        if (assignedTo == null) return "";
        return assignedTo;
    }
    public String getReportedBy() {
        if (reportedBy == null || "ANONIM".equals(reportedBy)) return "";
        return reportedBy;
    }

    // workaround because Jackson doesn't work well with LocalDate
    @JsonIgnore
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    @JsonProperty("createdAt")
    public String getCreatedAtAsString() {
        return createdAt.toString();
    }
    public List<Comment> getComments() { return comments; }

    public void upgradePriority() {
        if (this.status == StatusType.CLOSED) {
            return;
        }

        switch (this.businessPriority) {
            case LOW:
                this.businessPriority = BusinessPriorityType.MEDIUM;
                break;
            case MEDIUM:
                this.businessPriority = BusinessPriorityType.HIGH;
                break;
            case HIGH:
                this.businessPriority = BusinessPriorityType.CRITICAL;
                break;
            default:
                break;
        }
        validateSeniority(this.businessPriority);

    }

    private void validateSeniority(BusinessPriorityType businessPriority) {
        if (this.assignedTo == null || this.assignedTo.isEmpty()) {
            return;
        }
        User user = Database.getInstance().getUser(this.assignedTo);
        if (user.checkIfOverwhelmed(businessPriority)) {
            this.assignedTo = "";
            this.assignedAt = null;
            this.status = StatusType.OPEN;
        }
    }

    public void setBusinessPriority(BusinessPriorityType businessPriorityType) {
        this.businessPriority = businessPriorityType;
        validateSeniority(this.businessPriority);
    }
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setAssignedAt(LocalDate assignedAt) {
        this.assignedAt = assignedAt;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }
    public void setSolvedAt(LocalDate solvedAt) {
        this.solvedAt = solvedAt;
    }

    @JsonIgnore
    public List<TicketAction> getActions() { return actions; }

    public void addAction(TicketAction ticketAction) {
        this.actions.add(ticketAction);
    }

    @JsonIgnore
    public SeniorityType getRequiredSeniority() {
        switch (this.businessPriority) {
            case MEDIUM:
            case HIGH:
                return SeniorityType.MID;
            case CRITICAL:
                return SeniorityType.SENIOR;
            case LOW:
            default:
                return SeniorityType.JUNIOR;
        }
    }
    public abstract double accept(TicketVisitor visitor);
}
