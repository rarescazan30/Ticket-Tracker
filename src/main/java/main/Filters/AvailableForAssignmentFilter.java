package main.Filters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Database.Database;
import main.Enums.ExpertiseAreaType;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Filters.Filter;
import main.Milestone.Milestone;
import main.Ticket.Ticket;
import main.Users.Developer;
import main.Users.User;

public class AvailableForAssignmentFilter implements Filter<Ticket> {
    @Override
    public boolean matches(Ticket ticket, JsonNode filters, User user) {
        if (filters.has("availableForAssignment")) {
            boolean requested = filters.get("availableForAssignment").asBoolean();
            if (requested) {
                Developer dev = (Developer) user;
                if (!ticket.getStatus().equals(StatusType.OPEN))
                    return false;

                if (ticket.getAssignedTo() != null && !ticket.getAssignedTo().isEmpty())
                    return false;

                boolean inMilestone = false;
                for (Milestone m : Database.getInstance().getMilestones()) {
                    if (m.getTickets().contains(ticket.getId()) && m.getAssignedDevs().contains(dev.getUsername())) {
                        inMilestone = true;
                        break;
                    }
                }
                if (!inMilestone)  {
                    return false;
                }

                if (dev.getExpertiseArea() != ExpertiseAreaType.FULLSTACK) {
                    String ticketExpertise = ticket.getExpertiseArea().name();
                    String devExpertise = dev.getExpertiseArea().name();
                    if (!ticketExpertise.equals(devExpertise)) {
                        return false;
                    }
                }
                if (dev.getSeniority().ordinal() < ticket.getRequiredSeniority().ordinal()) {
                    return false;
                }
                return true;
            }
        }
        return true;
    }
}