package main.Visitor;

import main.Ticket.Bug;
import main.Ticket.FeatureRequest;
import main.Ticket.UIFeedback;
import main.Ticket.Ticket;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;

public class EfficiencyVisitor implements TicketVisitor {

    private double getDaysToResolve(Ticket t) {

        LocalDate start = LocalDate.parse(t.getAssignedAt().toString());
        if (t.getSolvedAt().toString().isEmpty()) return 1;
        LocalDate end = LocalDate.parse(t.getSolvedAt().toString());

        long days = ChronoUnit.DAYS.between(start, end) + 1;
        return (double)days;
    }

    @Override
    public double visit(Bug bug) {
        double days = getDaysToResolve(bug);
        double score = (bug.getFrequency().getValue() + bug.getSeverity().getValue()) * 10.0 / (days);
        return (score * 100.0) / 70.0;
    }

    @Override
    public double visit(FeatureRequest fr) {
        double days = getDaysToResolve(fr);
        double score = (fr.getBusinessValue().getValue() + fr.getCustomerDemand().getValue()) / days;
        return (score * 100.0) / 20.0;
    }

    @Override
    public double visit(UIFeedback ui) {
        double days = getDaysToResolve(ui);
        double score = (ui.getUsabilityScore() + ui.getBusinessValue().getValue()) / days;
        return (score * 100.0) / 20.0;
    }
}