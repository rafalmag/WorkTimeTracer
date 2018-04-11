package pl.rafalmag.worktimetracker;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import pl.rafalmag.worktimetracerlibrary.EventSourcingPersistenceManager;
import pl.rafalmag.worktimetracerlibrary.NotificationAwarePersistenceManager;
import pl.rafalmag.worktimetracerlibrary.PersistenceManager;
import pl.rafalmag.worktimetracerlibrary.WorkTimeTracerManager;
import pl.rafalmag.worktimetracerlibrary.notification.MainActivityClassProvider;

public class WorkTimeTrackerApp extends Application implements MainActivityClassProvider {

    private static final String TAG = WorkTimeTrackerApp.class.getCanonicalName();

    private WorkTimeTracerManager workTimeTracerManager;
    private PersistenceManager persistenceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        initExceptionHandler();
//        persistenceManager = new PreferencesPersistenceManager(this);
        persistenceManager = new NotificationAwarePersistenceManager(
                new EventSourcingPersistenceManager(this),
                this);
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
    @Override
    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }
    @Override
    public WorkTimeTracerManager getWorkTimeTracerManager() {
        return workTimeTracerManager;
    }

    @Override
    public Class<? extends Activity> getMainActivityClass() {
        return WorkTimeTracker.class;
    }
}

