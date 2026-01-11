package main.Ticket;

import main.Enums.BusinessValueType;
import main.Enums.CustomerDemandType;

public class UIFeedback extends Ticket {
    private String uiElementId;
    private BusinessValueType businessValue;
    private int usabilityScore; // int (1-10)
    private String screenshotUrl;
    private String suggestedFix;
}
