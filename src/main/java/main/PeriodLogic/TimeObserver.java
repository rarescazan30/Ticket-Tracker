package main.PeriodLogic;

import java.time.LocalDate;

/**
 * Interface defining the contract for objects that need to react to daily time updates
 * Acts as the Observer in the Observer design pattern
 */
public interface TimeObserver {
    /**
     * Callback method triggered by TimeManager whenever a day passes in the system
     */
    void onDayPassed(LocalDate currentDate);
}
