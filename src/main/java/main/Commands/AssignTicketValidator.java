package main.Commands;

import main.Database.Database;
import main.Enums.*;
import main.Exceptions.*;
import main.Milestone.Milestone;
import main.Ticket.Ticket;
import main.Users.Developer;
import main.Users.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssignTicketValidator {
    public void validateDeveloperExpertiseArea(int ticketId, String username) {
        Database database = Database.getInstance();
        User user = database.getUser(username);
        Developer dev = (Developer) user;
        ExpertiseAreaType devExpertise = dev.getExpertiseArea();
        ExpertiseType expertiseLevel = null;
        for (Ticket ticket : database.getTickets() ) {
            if (ticketId == ticket.getId()) {
                expertiseLevel = ticket.getExpertiseArea();
                break;
            }
        }
        if (expertiseLevel != null) {
            List<String> allowedAreas = new ArrayList<>();
            allowedAreas.add(ExpertiseAreaType.FULLSTACK.name());

            switch (expertiseLevel) {
                case FRONTEND:
                    allowedAreas.add(ExpertiseAreaType.FRONTEND.name());
                    break;
                case BACKEND:
                    allowedAreas.add(ExpertiseAreaType.BACKEND.name());
                    break;
                case DEVOPS:
                    allowedAreas.add(ExpertiseAreaType.DEVOPS.name());
                    break;
                case DESIGN:
                    allowedAreas.add(ExpertiseAreaType.DESIGN.name());
                    allowedAreas.add(ExpertiseAreaType.FRONTEND.name());
                    break;
                case DB:
                    allowedAreas.add(ExpertiseAreaType.DB.name());
                    allowedAreas.add(ExpertiseAreaType.BACKEND.name());
                    break;
            }

            if (!allowedAreas.contains(devExpertise.name())) {
                Collections.sort(allowedAreas);
                String requiredStr = String.join(", ", allowedAreas);

                throw new InvalidExpertiseException("Developer " + username + " cannot assign ticket " + ticketId +
                        " due to expertise area. Required: " + requiredStr +
                        "; Current: " + devExpertise + ".");
            }
        }
    }
    public void ValidateDeveloperExperienceLevel(int ticketId, String username) {
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

            if ((priority == BusinessPriorityType.LOW || priority == BusinessPriorityType.MEDIUM) && isBugOrFeedback) {
                allowedSeniorities.add(SeniorityType.JUNIOR.name());
            }

            if ((priority == BusinessPriorityType.LOW || priority == BusinessPriorityType.MEDIUM || priority == BusinessPriorityType.HIGH) && (isBugOrFeedback || isFeature)) {
                allowedSeniorities.add(SeniorityType.MID.name());
            }

            if (isBugOrFeedback || isFeature) {
                allowedSeniorities.add(SeniorityType.SENIOR.name());
            }

            if (!allowedSeniorities.contains(devSeniority.name())) {
                Collections.sort(allowedSeniorities);
                String requiredStr = String.join(", ", allowedSeniorities);

                throw new InvalidSeniorityException("Developer " + username + " cannot assign ticket " + ticketId +
                        " due to seniority level. Required: " + requiredStr +
                        "; Current: " + devSeniority + ".");
            }
        }
    }
    public void validateTicketStatus(int ticketId) {
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
    public void validateDeveloperAssignedToMilestone(int ticketId, String username) {
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
                throw new InvalidMilestoneAccessException("Developer " + username + " is not assigned to milestone " + milestone.getName() + ".");
            }
        }
    }
    public void validateMilestoneNotBlocked(int ticketId) {
        Database database = Database.getInstance();

        Milestone milestone = null;
        for (Milestone iterateMilestone : database.getMilestones()) {
            if (iterateMilestone.getTickets().contains(ticketId)) {
                milestone = iterateMilestone;
                break;
            }
        }

        if (milestone != null && milestone.getIsBlocked()) {
            throw new BlockedMilestoneException("Cannot assign ticket " + ticketId + " from blocked milestone " + milestone.getName() + ".");
        }
    }

    public void validate(int ticketId, String username) {
        validateDeveloperExpertiseArea(ticketId, username);
        ValidateDeveloperExperienceLevel(ticketId, username);
        validateTicketStatus(ticketId);
        validateDeveloperAssignedToMilestone(ticketId, username);
        validateMilestoneNotBlocked(ticketId);
    }
}
