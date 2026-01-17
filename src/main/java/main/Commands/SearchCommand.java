package main.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Database.Database;
import main.Enums.RoleType;
import main.Enums.StatusType;
import main.Filters.*;
import main.Output.OutputBuilder;
import main.Ticket.Ticket;
import main.Users.Developer;
import main.Users.Manager;
import main.Users.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Strategy DP
public class SearchCommand extends BaseCommand {
    private final List<Filter<Ticket>> ticketFilters = List.of(
            new AvailableForAssignmentFilter(),
            new CreatedAfterFilter(),
            new CreatedBeforeFilter(),
            new CreatedAtFilter(),
            new KeywordsFilter(),
            new TypeFilter(),
            new BusinessPriorityFilter()
    );

    private final List<Filter<User>> developerFilters = List.of(
            new ExpertiseAreaFilter(),
            new SeniorityFilter(),
            new PerformanceScoreAboveFilter(),
            new PerformanceScoreBelowFilter()
    );

    @Override
    protected List<RoleType> getAllowedRoles() {
        return List.of(RoleType.DEVELOPER, RoleType.MANAGER);
    }
    public SearchCommand(List<ObjectNode> outputs, JsonNode command) {
        super(outputs, command);
    }

    @Override
    public void executeLogic() {
        String username = command.get("username").asText();
        User user = Database.getInstance().getUser(username);
        JsonNode filters = command.get("filters");
        if (filters == null) {
            ViewTicketsCommand viewTickets = new ViewTicketsCommand(outputs, command);
            viewTickets.executeLogic();
            return;
        }
        String searchType = "TICKET";
        if (filters.has("searchType")) {
            searchType = filters.get("searchType").asText();
        }
        if (user.getRole() == RoleType.DEVELOPER) {
            searchType = "TICKET";
        }
        List<ObjectNode> resultsJsonObjects = new ArrayList<>();

        if (searchType.equals("TICKET")) {
            List<Ticket> results = new ArrayList<>();

            for (Ticket ticket : Database.getInstance().getTickets()) {
                if (ticket.getStatus()!= StatusType.OPEN) {
                    continue;
                }
                boolean matchesAll = true;
                for (Filter<Ticket> filter : ticketFilters) {
                    if (!filter.matches(ticket, filters, user)) {
                        matchesAll = false;
                        break;
                    }
                }
                if (matchesAll) {
                    results.add(ticket);
                }
            }
            results.sort(Comparator.comparing(Ticket::getCreatedAt).thenComparing(Ticket::getId));

            for (Ticket t : results) {
                ObjectNode tNode = mapper.createObjectNode();
                tNode.put("id", t.getId());
                tNode.put("type", t.getType().toString());
                tNode.put("title", t.getTitle());
                tNode.put("businessPriority", t.getBusinessPriority().toString());
                tNode.put("status", t.getStatus().toString());
                tNode.put("createdAt", t.getCreatedAt().toString());
                tNode.put("solvedAt", t.getSolvedAt() != null ? t.getSolvedAt().toString() : "");
                tNode.put("reportedBy", t.getReportedBy());
                List<String> foundWords = new ArrayList<>();
                if (filters.has("keywords")) {
                    String content = (t.getTitle() + " " + t.getDescription()).toLowerCase();
                    String[] words = content.split(" ");
                    for (JsonNode k : filters.get("keywords")) {
                        String key = k.asText().toLowerCase();
                        for (String word : words) {
                            if (word.contains(key)) {
                                foundWords.add(word);
                            }
                        }
                    }
                    Collections.sort(foundWords);

                }
                if (filters.has("keywords") || user.getRole() == RoleType.MANAGER) {
                    ArrayNode matchingWords = tNode.putArray("matchingWords");
                    foundWords.forEach(matchingWords::add);
                }
                resultsJsonObjects.add(tNode);
            }

        } else if (searchType.equals("DEVELOPER")) {
            List<User> results = new ArrayList<>();
            Manager manager = (Manager) user;
            for (User candidate : Database.getInstance().getUsers()) {
                if (candidate.getRole() != RoleType.DEVELOPER) {
                    continue;
                }
                if (!manager.getSubordinates().contains(candidate.getUsername())) {
                    continue;
                }

                boolean matchesAll = true;
                for (Filter<User> filter : developerFilters) {
                    if (!filter.matches(candidate, filters, user)) {
                        matchesAll = false;
                        break;
                    }
                }
                if (matchesAll) {
                    results.add(candidate);
                }
            }
            results.sort(Comparator.comparing(User::getUsername));

            for (User u : results) {
                Developer dev = (Developer) u;
                ObjectNode dNode = mapper.createObjectNode();
                dNode.put("username", dev.getUsername());
                dNode.put("expertiseArea", dev.getExpertiseArea().toString());
                dNode.put("seniority", dev.getSeniority().toString());
                 dNode.put("performanceScore", dev.getPerformanceScore());
                 dNode.put("hireDate", dev.getHireDate().toString());
                resultsJsonObjects.add(dNode);
            }
        }

        OutputBuilder builder = new OutputBuilder(mapper)
                .setCommand("search")
                .setUser(username)
                .setTimestamp(LocalDate.parse(command.get("timestamp").asText()))
                .put("searchType", searchType)
                .addData("results", resultsJsonObjects);

        outputs.add(builder.build());
    }
}