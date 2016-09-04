package pl.rafalmag.worktimetracker;

import android.app.Application;
import android.preference.PreferenceManager;
import android.util.Log;

import org.joda.time.Minutes;

import pl.rafalmag.worktimetracerlibrary.MinutesHolder;

public class WorkTimeTrackerApp extends Application {

    private static final String TAG = WorkTimeTrackerApp.class.getCanonicalName();
    public static final String TOTAL_OVER_HOURS_AS_MINUTES = "total_over_hours_as_mins";
    public static final String WORK_TIME = "work_time";
    public static final int DEFAULT_WORK_TIME_MINUTES = 8 * 60;

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
}

