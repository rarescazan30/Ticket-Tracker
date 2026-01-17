package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Enums.RoleType;
import main.Exceptions.InvalidPeriodException;
import main.PeriodLogic.Period;
import main.Ticket.Ticket;
import main.Ticket.TicketFactory;

import java.util.List;

/**
 * * Command responsible for reporting a new ticket
 * Enforces that tickets can only be created during active testing periods
 * */
public final class ReportTicketCommand extends BaseCommand {

    /**
     * * Returns the roles allowed to execute this command
     * Only Reporters can report new tickets
     * */
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.REPORTER);
    }

    /**
     * * Constructs the command with output buffer and input data
     * */
    public ReportTicketCommand(final List<ObjectNode> outputs, final JsonNode command) {
        super(outputs, command);
    }

    /**
     * * Executes the logic to create and store a new ticket
     * Validates the testing period and uses TicketFactory to create the appropriate ticket type
     * */
    @Override
    protected void executeLogic() {
        if (!Period.getInstance().isTestingPeriod(this.timestamp)) {
            throw new InvalidPeriodException("Tickets can only be reported during testing phases.");
        }
        int nextId = database.getNewTicketId();
        Ticket newTicket = TicketFactory.create(nextId, this.command);
        database.addTicket(newTicket);
    }
}
