package pl.rafalmag.worktimetracerlibrary.notification;

import android.app.Activity;

import pl.rafalmag.worktimetracerlibrary.PersistenceManager;
import pl.rafalmag.worktimetracerlibrary.WorkTimeTracerManager;

public interface MainActivityClassProvider {

    PersistenceManager getPersistenceManager();

    WorkTimeTracerManager getWorkTimeTracerManager();

    Class<? extends Activity> getMainActivityClass();
}
