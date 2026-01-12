package main.PeriodLogic;

import java.time.LocalDate;

public interface TimeObserver {
    void onDayPassed(LocalDate currentDate);
}
