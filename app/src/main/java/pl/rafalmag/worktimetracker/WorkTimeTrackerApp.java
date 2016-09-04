package pl.rafalmag.worktimetracker;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import pl.rafalmag.worktimetracerlibrary.MinutesHolder;
import pl.rafalmag.worktimetracerlibrary.Time;

public class WorkTimeTrackerApp extends Application {

    private static final String TAG = WorkTimeTrackerApp.class.getCanonicalName();
    public static final String TOTAL_OVER_HOURS_AS_MINUTES = "total_over_hours_as_mins";
    public static final String WORK_TIME = "work_time";
    public static final int DEFAULT_WORK_TIME_MINUTES = 8 * 60;

    private static final String START_HOUR = "START_HOUR";
    private static final String START_MINS = "START_MINS";
    private static final String STOP_HOUR = "STOP_HOUR";
    private static final String STOP_MINS = "STOP_MINS";

    private final MinutesHolder diffHolder = new MinutesHolder();

    @Override
    public void onCreate() {
        super.onCreate();
        initExceptionHandler();
    }

    private void initExceptionHandler() {
        final Thread.UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.e(TAG, "exception", ex);
                exceptionHandler.uncaughtException(thread, ex);
            }
        });
    }

    public MinutesHolder getDiffHolder() {
        return diffHolder;
    }

    public Minutes getNormalWorkHours() {
        int mins = PreferenceManager.getDefaultSharedPreferences(this).getInt(WORK_TIME, 8 * 60);
        return Minutes.minutes(mins);
    }

    public void saveOverHours(Minutes newOverHours) {
        int mins = newOverHours.getMinutes();
        boolean success = PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putInt(TOTAL_OVER_HOURS_AS_MINUTES, mins)
                // commit is blocking
                .commit();
        if (success) {
            Log.i(TAG, "Saved total over hours (" + mins + " mins)");
        } else {
            Log.e(TAG, "Could not save total over hours (" + mins + " mins)... :(");
        }
    }

    public Minutes getOverHours() {
        int mins = PreferenceManager.getDefaultSharedPreferences(this).getInt(TOTAL_OVER_HOURS_AS_MINUTES, 0);
        return Minutes.minutes(mins);
    }

    public void saveStartStopTime(Time startTime, Time stopTime) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putInt(START_HOUR, startTime.getHours());
        editor.putInt(START_MINS, startTime.getMinutes());
        editor.putInt(STOP_HOUR, stopTime.getHours());
        editor.putInt(STOP_MINS, stopTime.getMinutes());
        // apply is non-blocking
        editor.apply();
    }

    public Time getStartTime() {
        return getTime(START_HOUR, START_MINS);
    }
    public Time getStopTime() {
        return getTime(STOP_HOUR, STOP_MINS);
    }

    @NonNull
    private Time getTime(String hoursKey, String minutesKey) {
        DateTime currentTime = new DateTime();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int hours = preferences.getInt(hoursKey, currentTime.getHourOfDay());
        int minutes = preferences.getInt(minutesKey, currentTime.getMinuteOfHour());
        return new Time(hours,minutes);
    }
}

