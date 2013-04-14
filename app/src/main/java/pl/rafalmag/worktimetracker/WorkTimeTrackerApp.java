package pl.rafalmag.worktimetracker;

import android.app.Application;
import android.preference.PreferenceManager;
import android.util.Log;

import org.joda.time.Minutes;

/**
 * User: rafalmag
 * Date: 12.04.13
 * Time: 20:40
 */
public class WorkTimeTrackerApp extends Application {

    private static final String TAG = WorkTimeTrackerApp.class.getCanonicalName();
    private static final String TOTAL_OVER_HOURS_AS_MINUTES = "minutes";

    private final MinutesHolder overHoursHolder = new MinutesHolder();

    private final MinutesHolder diffHolder = new MinutesHolder();

    @Override
    public void onCreate() {
        super.onCreate();
        initExceptionHandler();
        loadOverHours();
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

    private void loadOverHours() {
        int mins = PreferenceManager.getDefaultSharedPreferences(this).getInt(TOTAL_OVER_HOURS_AS_MINUTES, 0);
        Minutes minutes = Minutes.minutes(mins);
        overHoursHolder.setMinutes(minutes);
        Log.i(TAG, "Loaded total over hours (" + mins + " mins)");
    }

    public void saveOverHours() {
        int mins = overHoursHolder.getMinutes().getMinutes();
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

    public MinutesHolder getOverHoursHolder() {
        return overHoursHolder;
    }

    public MinutesHolder getDiffHolder() {
        return diffHolder;
    }

    public Minutes getNormalWorkHours() {
        int mins = PreferenceManager.getDefaultSharedPreferences(this).getInt("work_time", 8 * 60);
        return Minutes.minutes(mins);
    }
}
