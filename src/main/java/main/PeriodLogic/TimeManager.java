package main.PeriodLogic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages system time and notifies observers of day changes
 * Implements Singleton and Observer design patterns
 */
public final class TimeManager {
    private static TimeManager singleton;
    private LocalDate lastSyncTime;
    private List<TimeObserver> observers = new ArrayList<>();

    private TimeManager() {
        // we keep this empty to implement the singleton
    }

    /**
     * Returns the singleton instance of the TimeManager
     */
    public static TimeManager getInstance() {
        if (singleton == null) {
            singleton = new TimeManager();
        }
        return singleton;
    }

    /**
     * Resets the singleton instance to null
     */
    public static void reset() {
        singleton = null;
    }

    /**
     * Adds a new time observer to the notification list
     */
    public void addObserver(final TimeObserver observer) {
        this.observers.add(observer);
    }

    /**
     * Synchronizes the system time and notifies observers for each elapsed day
     */
    public void sync(final LocalDate currentTime) {
        if (lastSyncTime == null) {
            lastSyncTime = currentTime;
            return;
        }
        LocalDate iteration = lastSyncTime.plusDays(1);
        while (!iteration.isAfter(currentTime)) {
            notifyObserver(iteration);
            iteration = iteration.plusDays(1);
        }
        lastSyncTime = currentTime;
    }

    /**
     * Implements the Observer pattern by iterating through
     * the observer list and triggering the onDayPassed callback for each subscriber
     */
    private void notifyObserver(final LocalDate currentTime) {
        for (TimeObserver observer : observers) {
            observer.onDayPassed(currentTime);
        }
    }
}
