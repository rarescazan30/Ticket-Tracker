package main.PeriodLogic;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Manages the testing period state within the system
 * Implements the Singleton design pattern
 */
public final class Period {
    private static final int TESTING_DURATION = 12;
    private static Period singleton;

    private LocalDate lastTestingStartDate;

    /**
     * Private constructor to prevent instantiation
     */
    public Period() {
        // we don't want to ever use it, we prevent instantiation
    }

    /**
     * Returns the singleton instance of the Period class
     */
    public static Period getInstance() {
        if (singleton == null) {
            singleton = new Period();
        }
        return singleton;
    }

    /**
     * Resets the testing period start date to null
     */
    public void reset() {
        this.lastTestingStartDate = null;
    }

    /**
     * Sets the start date for the testing period
     */
    public void startTestingPeriod(final LocalDate timestamp) {
        this.lastTestingStartDate = timestamp;
    }

    /**
     * Returns the date when the last testing period started
     */
    public LocalDate getLastTestingStartDate() {
        return lastTestingStartDate;
    }

    /**
     * Checks if the current date falls within a testing period
     */
    public boolean isTestingPeriod(final LocalDate currentDate) {
        if (lastTestingStartDate == null) {
            startTestingPeriod(currentDate);
            return true; // first command won't be a startTesting, but it works like one
        }
        int daysBetween = (int) ChronoUnit.DAYS.between(lastTestingStartDate, currentDate) + 1;
        return daysBetween <= TESTING_DURATION;
    }
}
