package main.PeriodLogic;

import main.Database.Database;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TimeManager {
    private static TimeManager singleton;
    private LocalDate lastSyncTime;
    private List<TimeObserver> observers = new ArrayList<>();
    private TimeManager() {
        // we keep this empty to implement the singleton
    }
    public static TimeManager getInstance() {
        if (singleton == null) {
            singleton = new TimeManager();
        }
        return singleton;
    }
    public static void reset() {
        singleton = null;
    }

    public void addObserver(TimeObserver observer) {
        this.observers.add(observer);
    }

    public void sync (LocalDate currentTime) {
        if (lastSyncTime == null) {
            lastSyncTime = currentTime;
            return;
        }
        LocalDate iteration =  lastSyncTime.plusDays(1);
        while (!iteration.isAfter(currentTime)) {
            notifyObserver(iteration);
            iteration = iteration.plusDays(1);
        }
        lastSyncTime = currentTime;
    }
    private void notifyObserver(LocalDate currentTime) {
        for (TimeObserver observer : observers) {
            observer.onDayPassed(currentTime);
        }
    }
}
