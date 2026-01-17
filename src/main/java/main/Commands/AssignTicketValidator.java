package main.Commands;

import main.Database.Database;
import main.Enums.BusinessPriorityType;
import main.Enums.ExpertiseAreaType;
import main.Enums.ExpertiseType;
import main.Enums.SeniorityType;
import main.Enums.StatusType;
import main.Exceptions.BlockedMilestoneException;
import main.Exceptions.InvalidExpertiseException;
import main.Exceptions.InvalidMilestoneAccessException;
import main.Exceptions.InvalidSeniorityException;
import main.Exceptions.InvalidStatusException;
import main.Milestone.Milestone;
import main.Ticket.Ticket;
import main.Users.Developer;
import main.Users.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * * Validates all conditions required to assign a ticket to a developer
 * Checks expertise, seniority, ticket status, and milestone constraints
 * */
public final class AssignTicketValidator {

    /**
     * * Verifies if the developer has the required expertise area for the ticket
     * */
    public void validateDeveloperExpertiseArea(final int ticketId, final String username) {
        Database database = Database.getInstance();
        User user = database.getUser(username);
        Developer dev = (Developer) user;
        ExpertiseAreaType devExpertise = dev.getExpertiseArea();
        ExpertiseType expertiseLevel = null;
        for (Ticket ticket : database.getTickets()) {
            if (ticketId == ticket.getId()) {
                expertiseLevel = ticket.getExpertiseArea();
                break;
            }
        }
        if (expertiseLevel != null) {
            List<String> allowedAreas = expertiseLevel.getCompatibleAreas();

            if (!allowedAreas.contains(devExpertise.name())) {
                Collections.sort(allowedAreas);
                String requiredStr = String.join(", ", allowedAreas);

                throw new InvalidExpertiseException("Developer " + username
                        + " cannot assign ticket " + ticketId
                        + " due to expertise area. Required: " + requiredStr
                        + "; Current: " + devExpertise + ".");
            }
        }
    }

    /**
     * * Verifies if the developer meets the seniority requirements based on ticket priority
     * */
    public void validateDeveloperExperienceLevel(final int ticketId, final String username) {
        Database database = Database.getInstance();
        User user = database.getUser(username);
        Developer dev = (Developer) user;
        SeniorityType devSeniority = dev.getSeniority();
        Ticket ticket = null;
        for (Ticket iterateTicket : database.getTickets()) {
            if (iterateTicket.getId() == ticketId) {
                ticket = iterateTicket;
                break;
            }
        }

        if (ticket != null) {
            List<String> allowedSeniorities = new ArrayList<>();
            BusinessPriorityType priority = ticket.getBusinessPriority();
            String type = ticket.getType();

            boolean isBugOrFeedback = type.equals("BUG") || type.equals("UI_FEEDBACK");
            boolean isFeature = type.equals("FEATURE_REQUEST");

            if ((priority == BusinessPriorityType.LOW
                    || priority == BusinessPriorityType.MEDIUM) && isBugOrFeedback) {
                allowedSeniorities.add(SeniorityType.JUNIOR.name());
            }

            if ((priority == BusinessPriorityType.LOW || priority == BusinessPriorityType.MEDIUM
                    || priority == BusinessPriorityType.HIGH)
                    && (isBugOrFeedback || isFeature)) {
                allowedSeniorities.add(SeniorityType.MID.name());
            }

            if (isBugOrFeedback || isFeature) {
                allowedSeniorities.add(SeniorityType.SENIOR.name());
            }

            if (!allowedSeniorities.contains(devSeniority.name())) {
                Collections.sort(allowedSeniorities);
                String requiredStr = String.join(", ", allowedSeniorities);

                throw new InvalidSeniorityException("Developer " + username
                        + " cannot assign ticket " + ticketId
                        + " due to seniority level. Required: " + requiredStr
                        + "; Current: " + devSeniority + ".");
            }
        }
    }

    /**
     * * Checks if the ticket is in OPEN status
     * */
    public void validateTicketStatus(final int ticketId) {
        Database database = Database.getInstance();
        Ticket ticket = null;

        for (Ticket iterateTicket : database.getTickets()) {
            if (iterateTicket.getId() == ticketId) {
                ticket = iterateTicket;
                break;
            }
        }

        if (ticket != null) {
            if (ticket.getStatus() != StatusType.OPEN) {
                throw new InvalidStatusException("Only OPEN tickets can be assigned.");
            }
        }
    }

    /**
     * * Ensures the developer is assigned to the milestone containing the ticket
     * */
    public void validateDeveloperAssignedToMilestone(final int ticketId, final String username) {
        Database database = Database.getInstance();

        Milestone milestone = null;
        for (Milestone iterateMilestone : database.getMilestones()) {
            if (iterateMilestone.getTickets().contains(ticketId)) {
                milestone = iterateMilestone;
                break;
            }
        }

        if (milestone != null) {
            if (!milestone.getAssignedDevs().contains(username)) {
                throw new InvalidMilestoneAccessException("Developer " + username
                        + " is not assigned to milestone " + milestone.getName() + ".");
            }
        }
    }

    /**
     * * Checks if the milestone associated with the ticket is not blocked
     * */
    public void validateMilestoneNotBlocked(final int ticketId) {
        Database database = Database.getInstance();

        Milestone milestone = null;
        for (Milestone iterateMilestone : database.getMilestones()) {
            if (iterateMilestone.getTickets().contains(ticketId)) {
                milestone = iterateMilestone;
                break;
            }
        }

        if (milestone != null && milestone.getIsBlocked()) {
            throw new BlockedMilestoneException("Cannot assign ticket " + ticketId
                    + " from blocked milestone " + milestone.getName() + ".");
        }
    }

    /**
     * * Runs all validation checks in sequence
     * */
    public void validate(final int ticketId, final String username) {
        validateDeveloperExpertiseArea(ticketId, username);
        validateDeveloperExperienceLevel(ticketId, username);
        validateTicketStatus(ticketId);
        validateDeveloperAssignedToMilestone(ticketId, username);
        validateMilestoneNotBlocked(ticketId);
    }
}
