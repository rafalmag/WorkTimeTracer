package pl.rafalmag.worktimetracker.wear;

import android.app.Application;
import android.util.Log;

import pl.rafalmag.worktimetracerlibrary.PersistenceManager;
import pl.rafalmag.worktimetracerlibrary.PreferencesPersistenceManager;
import pl.rafalmag.worktimetracerlibrary.WorkTimeTracerManager;

public class WorkTimeTrackerApp extends Application {

    private static final String TAG = WorkTimeTrackerApp.class.getCanonicalName();

    private WorkTimeTracerManager workTimeTracerManager;
    private PersistenceManager persistenceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        initExceptionHandler();
        persistenceManager = new PreferencesPersistenceManager(this);
        workTimeTracerManager = new WorkTimeTracerManager(persistenceManager);
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

    public WorkTimeTracerManager getWorkTimeTracerManager() {
        return workTimeTracerManager;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }
}

