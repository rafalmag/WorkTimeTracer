package pl.rafalmag.worktimetracerlibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.Observable;
import java.util.Observer;

public class WorkTimeTracerManager {

    private static final String TAG = WorkTimeTracerManager.class.getCanonicalName();

    public static final String TOTAL_OVER_HOURS_AS_MINUTES = "total_over_hours_as_mins";
    public static final String WORK_TIME = "work_time";
    private static final int DEFAULT_WORK_TIME_MINUTES = 8 * 60;
    private static final String START_HOUR = "START_HOUR";
    private static final String START_MINS = "START_MINS";
    private static final String STOP_HOUR = "STOP_HOUR";
    private static final String STOP_MINS = "STOP_MINS";
    private final MinutesHolder diffHolder = new MinutesHolder();
    private final TimeHolder startTimeHolder = new TimeHolder();
    private final TimeHolder stopTimeHolder = new TimeHolder();
    private final Context context;

    public WorkTimeTracerManager(Context context) {
        if (context == null) {
            throw new NullPointerException("Context cannot be null");
        }
        this.context = context;
        startTimeHolder.setTime(loadStartTime(context));
        stopTimeHolder.setTime(loadStopTime(context));
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

    private static Time loadStartTime(Context context) {
        return loadTime(START_HOUR, START_MINS, context);
    }

    private static Time loadStopTime(Context context) {
        return loadTime(STOP_HOUR, STOP_MINS, context);
    }

    @NonNull
    private static Time loadTime(String hoursKey, String minutesKey, Context context) {
        DateTime currentTime = new DateTime();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int hours = preferences.getInt(hoursKey, currentTime.getHourOfDay());
        int minutes = preferences.getInt(minutesKey, currentTime.getMinuteOfHour());
        return new Time(hours, minutes);
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

    public Minutes getNormalWorkHours() {
        int mins = PreferenceManager.getDefaultSharedPreferences(context).getInt(WORK_TIME, DEFAULT_WORK_TIME_MINUTES);
        return Minutes.minutes(mins);
    }

    public void saveOverHours(Minutes newOverHours) {
        int mins = newOverHours.getMinutes();
        boolean success = PreferenceManager.getDefaultSharedPreferences(context)
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
        int mins = PreferenceManager.getDefaultSharedPreferences(context).getInt(TOTAL_OVER_HOURS_AS_MINUTES, 0);
        return Minutes.minutes(mins);
    }

    public void saveStartStopTime(Time startTime, Time stopTime) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        startTimeHolder.setTime(startTime);
        editor.putInt(START_HOUR, startTime.getHours());
        editor.putInt(START_MINS, startTime.getMinutes());
        stopTimeHolder.setTime(stopTime);
        editor.putInt(STOP_HOUR, stopTime.getHours());
        editor.putInt(STOP_MINS, stopTime.getMinutes());
        // apply is non-blocking
        editor.apply();
    }

}