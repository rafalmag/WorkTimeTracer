package pl.rafalmag.worktimetracker.settings;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;

import pl.rafalmag.worktimetracerlibrary.PersistenceManager;
import pl.rafalmag.worktimetracerlibrary.PreferencesPersistenceManager;
import pl.rafalmag.worktimetracker.WorkTimeTrackerApp;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPreferences();
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    private void initPreferences() {
        PersistenceManager persistenceManager = ((WorkTimeTrackerApp) getApplication()).getPersistenceManager();
        int workTime = persistenceManager.getWorkTime().getMinutes();
        int overtime = persistenceManager.getOvertime().getMinutes();
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putInt(PreferencesPersistenceManager.WORK_TIME, workTime)
                .putInt(PreferencesPersistenceManager.TOTAL_OVER_HOURS_AS_MINUTES, overtime)
                .commit();
    }
}