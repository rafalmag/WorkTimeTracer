package pl.rafalmag.worktimetracerlibrary;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.sql.SQLException;

import pl.rafalmag.worktimetracerlibrary.db.StartStopEvent;
import pl.rafalmag.worktimetracerlibrary.db.WorkTimeTracerOpenHelper;

public class EventSourcingPersistenceManager implements PersistenceManager {

    private static final String TAG = EventSourcingPersistenceManager.class.getCanonicalName();

    private final WorkTimeTracerOpenHelper workTimeTracerOpenHelper;

    private Time startTime;
    private Time stopTime;
    private Minutes overtime = Minutes.minutes(0);
    private Minutes workTime = Minutes.minutes(8 * 60);

    public EventSourcingPersistenceManager(Context context) {
        if (context == null) {
            throw new NullPointerException("Context cannot be null");
        }
        workTimeTracerOpenHelper = OpenHelperManager.getHelper(context, WorkTimeTracerOpenHelper.class);
        initStartStopTime();
    }

    private void initStartStopTime() {
        try {
            Dao<StartStopEvent, Integer> dao = workTimeTracerOpenHelper.getDao(StartStopEvent.class);
            StartStopEvent startStopEvent = dao.queryBuilder().orderBy("date", false).queryForFirst();
            if (startStopEvent == null) {
                DateTime now = DateTime.now();
                startTime = new Time(now.getHourOfDay(), now.getMinuteOfHour());
                stopTime = new Time(now.getHourOfDay(), now.getMinuteOfHour());
            } else {
                startTime = startStopEvent.getStartTime();
                stopTime = startStopEvent.getStopTime();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not init start stop time, because of "
                    + e.getMessage(), e);
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
        try {
            Dao<StartStopEvent, Integer> dao = workTimeTracerOpenHelper.getDao(StartStopEvent.class);
            StartStopEvent event = new StartStopEvent(startTime, stopTime);
            dao.create(event);
        } catch (SQLException e) {
            Log.e(TAG, "Could not save saveOverHours event in db");
        }
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    @Override
    public void saveOvertime(Minutes newOverHours) {

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

    }
}
