package main.Ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import main.Comments.Comment;
import main.Database.Database;
import main.Enums.BusinessPriorityType;
import main.Enums.StatusType;
import main.Enums.ExpertiseType;
import main.Enums.SeniorityType;
import main.Exceptions.NonBugAnonymous;
import main.Users.User;
import main.Visitor.TicketVisitor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all ticket types in the system
 * Defines the common structure and lifecycle for bugs, features, and feedback
 */
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

    /**
     * Constructs a ticket from provided details and parses initial state
     */
    public Ticket(final int id, final String username,
                  final JsonNode ticketDetails, final String timestamp) {
        this.username = username;
        this.id = id;
        this.type = ticketDetails.get("type").asText();
        this.title = ticketDetails.get("title").asText();
        this.businessPriority = BusinessPriorityType
                .valueOf(ticketDetails.get("businessPriority").asText());
        this.status = StatusType.OPEN;
        this.expertiseArea = ExpertiseType.valueOf(ticketDetails.get("expertiseArea").asText());
        if (ticketDetails.has("description")) {
            this.description = ticketDetails.get("description").asText();
        }
        String reported = ticketDetails.get("reportedBy").asText();
        if (!reported.isEmpty()) {
            this.reportedBy = reported;
        } else {
            if (this.type.equals("BUG")) {
                this.reportedBy = "ANONIM";
                this.businessPriority = BusinessPriorityType.LOW;
            } else {
                throw new NonBugAnonymous("Anonymous reports are only allowed"
                        + " for tickets of type BUG.");
            }
        }
        this.createdAt = LocalDate.parse(timestamp);
        this.comments = new ArrayList<>();
    }

    /**
     * Returns the unique identifier
     */
    public final int getId() {
        return id;
    }

    /**
     * Returns the ticket category
     */
    public final String getType() {
        return type;
    }

    /**
     * Returns the short title
     */
    public final String getTitle() {
        return title;
    }

    /**
     * Returns the current priority
     */
    public final BusinessPriorityType getBusinessPriority() {
        return businessPriority;
    }

    /**
     * Returns current status
     */
    public final StatusType getStatus() {
        return status;
    }

    /**
     * Returns area of expertise
     */
    @JsonIgnore
    public final ExpertiseType getExpertiseArea() {
        return expertiseArea;
    }

    /**
     * Returns full description
     */
    @JsonIgnore
    public final String getDescription() {
        return description;
    }

    /**
     * Returns string representation of solved date
     */
    public final Object getSolvedAt() {
        if (solvedAt == null) {
            return "";
        }
        return solvedAt.toString();
    }

    /**
     * Returns string representation of assigned date
     */
    public final Object getAssignedAt() {
        if (assignedAt == null) {
            return "";
        }
        return assignedAt.toString();
    }

    /**
     * Returns assignee username
     */
    public final String getAssignedTo() {
        if (assignedTo == null) {
            return "";
        }
        return assignedTo;
    }

    /**
     * Returns reporter username, handles anonymity
     */
    public final String getReportedBy() {
        if (reportedBy == null || "ANONIM".equals(reportedBy)) {
            return "";
        }
        return reportedBy;
    }

    /**
     * Returns creation date
     */
    // workaround because Jackson doesn't work well with LocalDate
    @JsonIgnore
    public final LocalDate getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns creation date a String
     */
    @JsonProperty("createdAt")
    public final String getCreatedAtAsString() {
        return createdAt.toString();
    }

    /**
     * Returns associated comments
     */
    public final List<Comment> getComments() {
        return comments;
    }

    /**
     * Increments ticket priority and validates if assignee is overwhelmed
     */
    public final void upgradePriority(final LocalDate currentDate) {
        if (this.status == StatusType.CLOSED) {
            return;
        }
        this.businessPriority = this.businessPriority.getNext();
        validateSeniority(this.businessPriority, currentDate);
    }

    /**
     * Logic for unassigning overwhelmed developers
     */
    private void validateSeniority(final BusinessPriorityType priority,
                                   final LocalDate currentDate) {
        if (this.assignedTo == null || this.assignedTo.isEmpty()) {
            return;
        }
        User user = Database.getInstance().getUser(this.assignedTo);
        if (user.checkIfOverwhelmed(priority)) {
            this.assignedTo = "";
            this.assignedAt = null;
            this.status = StatusType.OPEN;
            if (getSolvedAt() != null) {
                this.actions.add(new SystemRemovalAction(user.getUsername(), currentDate));
            }
        }
    }

    /**
     * Sets business priority and re-validates seniority
     */
    public final void setBusinessPriority(final BusinessPriorityType businessPriorityType) {
        this.businessPriority = businessPriorityType;
        validateSeniority(this.businessPriority, LocalDate.parse(getCreatedAtAsString()));
    }

    /**
     * Sets the assignee
     */
    public final void setAssignedTo(final String assignedTo) {
        this.assignedTo = assignedTo;
    }

    /**
     * Sets assignment date
     */
    public final void setAssignedAt(final LocalDate assignedAt) {
        this.assignedAt = assignedAt;
    }

    /**
     * Updates ticket status
     */
    public final void setStatus(final StatusType status) {
        this.status = status;
    }

    /**
     * Sets resolution date
     */
    public final void setSolvedAt(final LocalDate solvedAt) {
        this.solvedAt = solvedAt;
    }

    /**
     * Returns action history
     */
    @JsonIgnore
    public final List<TicketAction> getActions() {
        return actions;
    }

    /**
     * Adds a new action to the log
     */
    public final void addAction(final TicketAction ticketAction) {
        this.actions.add(ticketAction);
    }

    /**
     * Calculates required seniority based on priority
     */
    @JsonIgnore
    public final SeniorityType getRequiredSeniority() {
        return this.businessPriority.getRequiredSeniority();
    }

    /**
     * Entry point for TicketVisitor implementations
     */
    public abstract double accept(TicketVisitor visitor);
}
