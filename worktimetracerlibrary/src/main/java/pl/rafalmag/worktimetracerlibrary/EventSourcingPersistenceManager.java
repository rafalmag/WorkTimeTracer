package pl.rafalmag.worktimetracerlibrary;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.joda.time.Minutes;

import java.sql.SQLException;
import java.util.List;

import pl.rafalmag.worktimetracerlibrary.db.Event;
import pl.rafalmag.worktimetracerlibrary.db.EventParser;
import pl.rafalmag.worktimetracerlibrary.db.LogTimeEvent;
import pl.rafalmag.worktimetracerlibrary.db.OvertimeUpdatedEvent;
import pl.rafalmag.worktimetracerlibrary.db.StartStopUpdatedEvent;
import pl.rafalmag.worktimetracerlibrary.db.WorkTimeTracerOpenHelper;
import pl.rafalmag.worktimetracerlibrary.db.WorkTimeUpdatedEvent;

public class EventSourcingPersistenceManager implements PersistenceManager {

    private static final String TAG = EventSourcingPersistenceManager.class.getCanonicalName();

    private final WorkTimeTracerOpenHelper workTimeTracerOpenHelper;
    private EventParser eventParser = new EventParser();

    private final ValueHolder valueHolder;

    public static class ValueHolder {
        private Time startTime;
        private Time stopTime;
        private Minutes overtime;
        private Minutes workTime;

        public Time getStartTime() {
            return startTime;
        }

        public void setStartTime(Time startTime) {
            this.startTime = startTime;
        }

        public Time getStopTime() {
            return stopTime;
        }

        public void setStopTime(Time stopTime) {
            this.stopTime = stopTime;
        }

        public Minutes getOvertime() {
            return overtime;
        }

        public void setOvertime(Minutes overtime) {
            this.overtime = overtime;
        }

        public Minutes getWorkTime() {
            return workTime;
        }

        public void setWorkTime(Minutes workTime) {
            this.workTime = workTime;
        }
    }

    public EventSourcingPersistenceManager(Context context) {
        if (context == null) {
            throw new NullPointerException("Context cannot be null");
        }
        workTimeTracerOpenHelper = OpenHelperManager.getHelper(context, WorkTimeTracerOpenHelper.class);

        Dao<Event, Integer> dao = workTimeTracerOpenHelper.getEventDao();
        valueHolder = new ValueHolder();
        try {
            List<Event> events = dao
                    .queryBuilder()
                    .orderBy("date", true) // ascending
                    .query();
            for (Event event : events) {
                Event parsedEvent = eventParser.parseEvent(event);
                parsedEvent.applyTo(valueHolder);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load events, because of " + e.getMessage(), e);
        }
    }

    @Override
    public Time loadStartTime() {
        return valueHolder.getStartTime();
    }

    @Override
    public Time loadStopTime() {
        return valueHolder.getStopTime();
    }

    @Override
    public void saveStartStopTime(Time startTime, Time stopTime) {
        if (startTime.equals(loadStartTime()) && stopTime.equals(loadStopTime())) {
            Log.d(TAG, "Start stop time did not change, nothing to save");
            return;
        }
        try {
            Dao<Event, Integer> dao = workTimeTracerOpenHelper.getDao(Event.class);
            StartStopUpdatedEvent startStopUpdatedEvent = new StartStopUpdatedEvent(startTime, stopTime);
            dao.create(startStopUpdatedEvent);
            startStopUpdatedEvent.applyTo(valueHolder);
        } catch (SQLException e) {
            Log.e(TAG, "Could not save saveOverHours event in db, because of " + e.getMessage(), e);
        }
    }

    @Override
    public void saveOvertime(Minutes overtime) {
        try {
            Dao<Event, Integer> dao = workTimeTracerOpenHelper.getDao(Event.class);
            OvertimeUpdatedEvent overtimeUpdatedEvent = new OvertimeUpdatedEvent(getOvertime(), overtime);
            dao.create(overtimeUpdatedEvent);
            overtimeUpdatedEvent.applyTo(valueHolder);
        } catch (SQLException e) {
            Log.e(TAG, "Could not save overtime event in db, because of " + e.getMessage(), e);
        }
    }

    @Override
    public void logWork() {
        try {
            Dao<Event, Integer> dao = workTimeTracerOpenHelper.getDao(Event.class);
            LogTimeEvent logTimeEvent = new LogTimeEvent(
                    valueHolder.getStartTime(),
                    valueHolder.getStopTime(),
                    valueHolder.getWorkTime(),
                    valueHolder.getOvertime());
            dao.create(logTimeEvent);
            logTimeEvent.applyTo(valueHolder);
        } catch (SQLException e) {
            Log.e(TAG, "Could not save overtime event in db, because of " + e.getMessage(), e);
        }
    }

    @Override
    public Minutes getOvertime() {
        return valueHolder.getOvertime();
    }

    @Override
    public Minutes getWorkTime() {
        return valueHolder.getWorkTime();
    }

    @Override
    public void saveWorkTime(Minutes workTime) {
        try {
            Dao<Event, Integer> dao = workTimeTracerOpenHelper.getDao(Event.class);
            WorkTimeUpdatedEvent workTimeUpdatedEvent = new WorkTimeUpdatedEvent(workTime);
            dao.create(workTimeUpdatedEvent);
            workTimeUpdatedEvent.applyTo(valueHolder);
        } catch (SQLException e) {
            Log.e(TAG, "Could not save work time event in db");
        }
    }
}
