package main.Ticket;

import main.Enums.BusinessPriorityType;
import main.Enums.ExpertiseType;
import main.Enums.StatusType;

/*
word
* */
public class Ticket {
    private int id;
    private String type;
    private String title;
    private BusinessPriorityType businessPriority;
    private StatusType status;
    private ExpertiseType expertiseArea;
    private String description;
    private String reportedBy;
}
