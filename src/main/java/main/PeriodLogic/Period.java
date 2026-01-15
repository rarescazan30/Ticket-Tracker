package main.PeriodLogic;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/*
* singleton DP
* */
public class Period {
    private static Period singleton;

    private LocalDate lastTestingStartDate = null;
    public Period() {
        // we don't want to ever use it
    }
    public static Period getInstance() {
        if (singleton == null) {
            singleton = new Period();
        }
        return singleton;
    }
    public void reset() {
        this.lastTestingStartDate = null;
    }
    public void startTestingPeriod(LocalDate timestamp) {
        this.lastTestingStartDate = timestamp;
    }
    public LocalDate getLastTestingStartDate() {
        return lastTestingStartDate;
    }
    public boolean isTestingPeriod (LocalDate currentDate) {
        if (lastTestingStartDate == null) {
            startTestingPeriod(currentDate);
            return true; //first command won't be a startTesting, but it works like one.
        }
        int daysBetween = (int) ChronoUnit.DAYS.between(lastTestingStartDate, currentDate) + 1;
        return daysBetween <= 12;
    }
}
