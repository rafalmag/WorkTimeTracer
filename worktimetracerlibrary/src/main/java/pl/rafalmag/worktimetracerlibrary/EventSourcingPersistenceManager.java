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

    private Time startTime;
    private Time stopTime;
    private Minutes overtime;
    private Minutes workTime;
    private final ValueAccessor valueSetter;

    public class ValueAccessor {

        public Minutes getWorkTime() {
            return workTime;
        }

        public Time getStartTime() {
            return startTime;
        }

        public Time getStopTime() {
            return stopTime;
        }

        public void setStartTime(Time startTime) {
            EventSourcingPersistenceManager.this.startTime = startTime;
        }

        public void setStopTime(Time stopTime) {
            EventSourcingPersistenceManager.this.stopTime = stopTime;
        }

        public void setOvertime(Minutes overtime) {
            EventSourcingPersistenceManager.this.overtime = overtime;
        }

        public void setWorkTime(Minutes workTime) {
            EventSourcingPersistenceManager.this.workTime = workTime;
        }

        public Minutes getOvertime() {
            return overtime;
        }
    }

    public EventSourcingPersistenceManager(Context context) {
        if (context == null) {
            throw new NullPointerException("Context cannot be null");
        }
        workTimeTracerOpenHelper = OpenHelperManager.getHelper(context, WorkTimeTracerOpenHelper.class);

        Dao<Event, Integer> dao = workTimeTracerOpenHelper.getEventDao();
        valueSetter = new ValueAccessor();
        try {
            List<Event> events = dao
                    .queryBuilder()
                    .orderBy("date", true) // ascending
                    .query();
            for (Event event : events) {
                Event parsedEvent = eventParser.parseEvent(event);
                parsedEvent.apply(valueSetter);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load events, because of " + e.getMessage(), e);
        }
    }

    @Override
    public Time loadStartTime() {
        return startTime;
    }

    @Override
    public Time loadStopTime() {
        return stopTime;
    }

    @Override
    public void saveStartStopTime(Time startTime, Time stopTime) {
        if (startTime.equals(this.startTime) && stopTime.equals(this.stopTime)) {
            Log.d(TAG, "Start stop time did not change, nothing to save");
            return;
        }
        try {
            Dao<Event, Integer> dao = workTimeTracerOpenHelper.getDao(Event.class);
            StartStopUpdatedEvent startStopUpdatedEvent = new StartStopUpdatedEvent(startTime, stopTime);
            dao.create(startStopUpdatedEvent);
            startStopUpdatedEvent.apply(valueSetter);
        } catch (SQLException e) {
            Log.e(TAG, "Could not save saveOverHours event in db, because of " + e.getMessage(), e);
        }
    }

    @Override
    public void saveOvertime(Minutes overtime) {
        try {
            Dao<Event, Integer> dao = workTimeTracerOpenHelper.getDao(Event.class);
            OvertimeUpdatedEvent overtimeUpdatedEvent = new OvertimeUpdatedEvent(this.overtime, overtime);
            dao.create(overtimeUpdatedEvent);
            overtimeUpdatedEvent.apply(valueSetter);
        } catch (SQLException e) {
            Log.e(TAG, "Could not save overtime event in db, because of " + e.getMessage(), e);
        }
    }

    @Override
    public void logWork() {
        try {
            Dao<Event, Integer> dao = workTimeTracerOpenHelper.getDao(Event.class);
            LogTimeEvent logTimeEvent = new LogTimeEvent();
            dao.create(logTimeEvent);
            logTimeEvent.apply(valueSetter);
        } catch (SQLException e) {
            Log.e(TAG, "Could not save overtime event in db, because of " + e.getMessage(), e);
        }
    }

    @Override
    public Minutes getOvertime() {
        return overtime;
    }

    @Override
    public Minutes getWorkTime() {
        return workTime;
    }

    @Override
    public void saveWorkTime(Minutes workTime) {
        try {
            Dao<Event, Integer> dao = workTimeTracerOpenHelper.getDao(Event.class);
            WorkTimeUpdatedEvent workTimeUpdatedEvent = new WorkTimeUpdatedEvent(workTime);
            dao.create(workTimeUpdatedEvent);
            workTimeUpdatedEvent.apply(valueSetter);
        } catch (SQLException e) {
            Log.e(TAG, "Could not save work time event in db");
        }
    }
}
