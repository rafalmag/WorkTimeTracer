package pl.rafalmag.worktimetracerlibrary;


import org.joda.time.Minutes;

public interface PersistenceManager {
    Time loadStartTime();

    Time loadStopTime();

    void saveStartStopTime(Time startTime, Time stopTime);

    void saveOvertime(Minutes newOverHours);

    Minutes getOvertime();

    Minutes getWorkTime();

    void saveWorkTime(Minutes workTime);
}
