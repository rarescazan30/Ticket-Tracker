package main.Ticket;

import jdk.jfr.Frequency;
import main.Enums.FrequencyType;
import main.Enums.SeverityType;

public class Bug extends Ticket {
    private String expectedBehaviour;
    private String actualBehaviour;
    private FrequencyType frequency;
    private SeverityType severity;
    private String environment;
    private int errorCode;

}
