package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Enums.RoleType;
import main.Exceptions.InvalidPeriodException;
import main.Exceptions.StopExecutionException;
import main.PeriodLogic.Period;
import main.Ticket.Ticket;
import main.Ticket.TicketFactory;

import java.util.List;

public class ReportTicketCommand extends BaseCommand {
    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.REPORTER);
    }

    public ReportTicketCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }

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
