package main.Milestone;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Milestone {
    private String name;
    private List<String> blockingFor = new ArrayList<>();
    private LocalDate dueDate;
    private List<Integer> tickets = new ArrayList<>();
    private List<String> assignedDevs =  new ArrayList<>();
    private String createdBy;
}
