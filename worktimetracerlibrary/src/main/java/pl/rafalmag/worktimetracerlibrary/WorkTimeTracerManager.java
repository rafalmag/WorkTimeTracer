package pl.rafalmag.worktimetracerlibrary;

import org.joda.time.Minutes;

import java.util.Observable;
import java.util.Observer;

public class WorkTimeTracerManager {

    private final MinutesHolder diffHolder = new MinutesHolder();
    private final MinutesHolder overtimeHolder = new MinutesHolder();
    private final MinutesHolder workTimeHolder = new MinutesHolder();
    private final TimeHolder startTimeHolder = new TimeHolder();
    private final TimeHolder stopTimeHolder = new TimeHolder();

    private final PersistenceManager persistenceManager;

    public WorkTimeTracerManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
        startTimeHolder.setTime(persistenceManager.loadStartTime());
        stopTimeHolder.setTime(persistenceManager.loadStopTime());
        overtimeHolder.setMinutes(persistenceManager.getOvertime());
        workTimeHolder.setMinutes(persistenceManager.getWorkTime());
        Observer observer = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Minutes diff = DateUtils.diff(startTimeHolder.getTime(), stopTimeHolder.getTime());
                diffHolder.setMinutes(diff);
            }
        };
        startTimeHolder.addObserver(observer);
        stopTimeHolder.addObserver(observer);
    }

    public MinutesHolder getDiffHolder() {
        return diffHolder;
    }

    public TimeHolder getStartTimeHolder() {
        return startTimeHolder;
    }

    public TimeHolder getStopTimeHolder() {
        return stopTimeHolder;
    }

    public void saveStartStopTime(Time startTime, Time stopTime) {
        startTimeHolder.setTime(startTime);
        stopTimeHolder.setTime(stopTime);
        persistenceManager.saveStartStopTime(startTime, stopTime);
    }

    public MinutesHolder getOvertimeHolder() {
        return overtimeHolder;
    }

    public MinutesHolder getWorkTimeHolder() {
        return workTimeHolder;
    }
}