package main.Ticket;

import com.fasterxml.jackson.databind.JsonNode;
import jdk.jshell.Snippet;
import main.Comments.Comment;
import main.Enums.BusinessPriorityType;
import main.Enums.ExpertiseType;
import main.Enums.StatusType;
import main.Exceptions.NonBugAnonymous;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
word
* */
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

    public Ticket (int id, String username, JsonNode ticketDetails) {
        this.username = username;
        this.id = id;
        this.type = ticketDetails.get("type").asText();
        this.title = ticketDetails.get("title").asText();
        this.businessPriority = BusinessPriorityType.valueOf(ticketDetails.get("businessPriority").asText());
        this.status = StatusType.OPEN;
        this.expertiseArea = ExpertiseType.valueOf(ticketDetails.get("expertiseArea").asText());
        this.description = ticketDetails.get("description").asText();
        if  (!ticketDetails.get("reportedBy").asText().isEmpty()) {
            this.reportedBy = ticketDetails.get("reportedBy").asText();
        } else {
            if (this.type.equals("BUG")) {
                this.reportedBy = "ANONIM";
                this.businessPriority = BusinessPriorityType.LOW;
            } else throw new NonBugAnonymous("ERROR - Ticket is not BUG!");
        }
        this.createdAt = LocalDate.parse(ticketDetails.get("timestamp").asText());
        this.comments = new ArrayList<>();
        this.solvedAt = null;
        this.assignedAt = null;
        this.assignedTo = null;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public BusinessPriorityType getBusinessPriority() { return businessPriority; }
    public StatusType getStatus() { return status; }
    public ExpertiseType getExpertiseArea() { return expertiseArea; }
    public String getDescription() { return description; }
    public String getReportedBy() { return reportedBy; }

    public LocalDate getCreatedAt() { return createdAt; }
    public LocalDate getAssignedAt() { return assignedAt; }
    public String getAssignedTo() { return assignedTo; }
    public List<Comment> getComments() { return comments; }
    public Object getSolvedAt() {
        if (solvedAt == null) return "";
        return solvedAt;
    }
}
