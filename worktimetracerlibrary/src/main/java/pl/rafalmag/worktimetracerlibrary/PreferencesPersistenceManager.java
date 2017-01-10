package pl.rafalmag.worktimetracerlibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

public class PreferencesPersistenceManager implements PersistenceManager {

    private static final String TAG = PreferencesPersistenceManager.class.getCanonicalName();

    private static final String START_HOUR = "START_HOUR";
    private static final String START_MINS = "START_MINS";
    private static final String STOP_HOUR = "STOP_HOUR";
    private static final String STOP_MINS = "STOP_MINS";
    public static final String TOTAL_OVER_HOURS_AS_MINUTES = "total_over_hours_as_mins";

    public static final String WORK_TIME = "work_time";
    private static final int DEFAULT_WORK_TIME_MINUTES = 8 * 60;

    private final Context context;

    public PreferencesPersistenceManager(Context context) {
        if (context == null) {
            throw new NullPointerException("Context cannot be null");
        }
        this.context = context;
    }

    @Override
    public Time loadStartTime() {
        return loadTime(START_HOUR, START_MINS);
    }

    @Override
    public Time loadStopTime() {
        return loadTime(STOP_HOUR, STOP_MINS);
    }

    @NonNull
    private Time loadTime(String hoursKey, String minutesKey) {
        DateTime currentTime = new DateTime();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int hours = preferences.getInt(hoursKey, currentTime.getHourOfDay());
        int minutes = preferences.getInt(minutesKey, currentTime.getMinuteOfHour());
        return new Time(hours, minutes);
    }

    @Override
    public void saveStartStopTime(Time startTime, Time stopTime) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(START_HOUR, startTime.getHours());
        editor.putInt(START_MINS, startTime.getMinutes());
        editor.putInt(STOP_HOUR, stopTime.getHours());
        editor.putInt(STOP_MINS, stopTime.getMinutes());
        // apply is non-blocking
        editor.apply();
    }

    @Override
    public void saveOvertime(Minutes newOverHours) {
        int mins = newOverHours.getMinutes();
        boolean success = PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(TOTAL_OVER_HOURS_AS_MINUTES, mins)
                // commit is blocking
                .commit();
        if (success) {
            Log.i(TAG, "Saved total overtime (" + mins + " mins)");
        } else {
            Log.e(TAG, "Could not save total overtime (" + mins + " mins)... :(");
        }
    }

    @Override
    public void logWork() {
        Minutes diff = DateUtils.diff(loadStartTime(), loadStopTime());
        Minutes todayOvertime = diff.minus(getWorkTime());
        Minutes totalOvertime = getOvertime();
        Minutes newOvertime = totalOvertime.plus(todayOvertime);
        Log.i(TAG, "Logging work new overtime " + DateUtils.minutesToText(newOvertime)
                + ", diff " + DateUtils.minutesToText(diff));
        saveOvertime(newOvertime);
    }

    @Override
    public Minutes getOvertime() {
        int mins = PreferenceManager.getDefaultSharedPreferences(context).getInt(TOTAL_OVER_HOURS_AS_MINUTES, 0);
        return Minutes.minutes(mins);
    }

    @Override
    public Minutes getWorkTime() {
        int mins = PreferenceManager.getDefaultSharedPreferences(context).getInt(WORK_TIME, DEFAULT_WORK_TIME_MINUTES);
        return Minutes.minutes(mins);
    }

    @Override
    public void saveWorkTime(Minutes workTime) {
        int mins = workTime.getMinutes();
        boolean success = PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(WORK_TIME, mins)
                // commit is blocking
                .commit();
        if (success) {
            Log.i(TAG, "Saved work time (" + mins + " mins)");
        } else {
            Log.e(TAG, "Could not save work time (" + mins + " mins)... :(");
        }
    }

}
