package pl.rafalmag.worktimetracker.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.Minutes;

import pl.rafalmag.worktimetracerlibrary.DateUtils;
import pl.rafalmag.worktimetracerlibrary.PreferencesPersistenceManager;
import pl.rafalmag.worktimetracker.R;
import pl.rafalmag.worktimetracker.WorkTimeTrackerApp;

public class SettingsFragment extends PreferenceFragment {

    private static final String TAG = SettingsFragment.class.getCanonicalName();

    // to keep it from GC
    // http://stackoverflow.com/questions/2542938/sharedpreferences-onsharedpreferencechangelistener-not-being-called
    // -consistently
    private final SharedPreferences.OnSharedPreferenceChangeListener overtimePreferenceChangeListener = new SharedPreferences
            .OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(PreferencesPersistenceManager.TOTAL_OVER_HOURS_AS_MINUTES)) {
                // in event sourcing here event could be produced
                Minutes minutes = Minutes.minutes(sharedPreferences.getInt(key, 0));
                Log.d(TAG, key + " changed to " + DateUtils.minutesToText(minutes));
                // first set in persistence manager
                ((WorkTimeTrackerApp) getActivity().getApplication()).getPersistenceManager().saveOvertime(minutes);
                // this will refresh view
                ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager().getOvertimeHolder().setMinutes(minutes);
            }
        }
    };
    // strong reference
    private final SharedPreferences.OnSharedPreferenceChangeListener workTimePreferenceChangeListenerForDiff = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(PreferencesPersistenceManager.WORK_TIME)) {
                // in event sourcing here event could be produced
                Minutes minutes = Minutes.minutes(sharedPreferences.getInt(key, 0));
                Log.d(TAG, key + " changed to " + DateUtils.minutesToText(minutes));
                // first set in persistence manager
                ((WorkTimeTrackerApp) getActivity().getApplication()).getPersistenceManager().saveWorkTime(minutes);
                // this will refresh view, which uses value from persistence manager
                ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager().getWorkTimeHolder().setMinutes(minutes);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(workTimePreferenceChangeListenerForDiff);
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(overtimePreferenceChangeListener);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(workTimePreferenceChangeListenerForDiff);
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(overtimePreferenceChangeListener);
    }

}
