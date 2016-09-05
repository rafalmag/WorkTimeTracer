package pl.rafalmag.worktimetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.rafalmag.worktimetracerlibrary.DateUtils;
import pl.rafalmag.worktimetracerlibrary.MinutesHolder;
import pl.rafalmag.worktimetracerlibrary.NonScrollableTimePicker;
import pl.rafalmag.worktimetracerlibrary.Time;
import pl.rafalmag.worktimetracerlibrary.WorkTimeTracerManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class WorkTimeTrackerFragment extends Fragment {

    private static final String TAG = WorkTimeTracker.class.getCanonicalName();

    @BindView(R.id.startTimePicker)
    NonScrollableTimePicker startTimePicker;

    @BindView(R.id.stopTimePicker)
    NonScrollableTimePicker stopTimePicker;

    @BindView(R.id.log)
    Button logButton;

    @BindView(R.id.overHours)
    TextView overHours;

    @BindView(R.id.diff_time)
    TextView diffText;

    private OnTimeChangedListener onTimeChangedListener;
    // to keep it from GC
    // http://stackoverflow.com/questions/2542938/sharedpreferences-onsharedpreferencechangelistener-not-being-called
    // -consistently
    private final SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences
            .OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(WorkTimeTracerManager.TOTAL_OVER_HOURS_AS_MINUTES)) {
                Log.d(TAG, key + " changed");
                Minutes minutes = Minutes.minutes(sharedPreferences.getInt(key, 0));
                updateOverHoursText(minutes);
            }
        }
    };
    // strong reference
    private final SharedPreferences.OnSharedPreferenceChangeListener workTimePreferenceChangeListenerForDiff = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            final MinutesHolder diffHolder = ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager().getDiffHolder();
            if (key.equals(WorkTimeTracerManager.WORK_TIME)) {
                updateDiffText(diffHolder.getMinutes());
            }
        }
    };
    private final Observer diffHolderObserver = new Observer() {
        @Override
        public void update(Observable observable, Object data) {
            updateDiffText((Minutes) data);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_time_tracker, container, false);
        ButterKnife.bind(this, view);
        onTimeChangedListener = new OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                WorkTimeTracerManager workTimeTracerManager = ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager();
                Time startTime = new Time(startTimePicker.getHour(), startTimePicker.getMinute());
                workTimeTracerManager.getStartTimeHolder().setTime(startTime);
                Time stopTime = new Time(stopTimePicker.getHour(), stopTimePicker.getMinute());
                workTimeTracerManager.getStopTimeHolder().setTime(stopTime);
            }
        };

        initTimePickers();
        initOverHoursText();
        initDiffText();
        logButton.setHapticFeedbackEnabled(true);
        return view;
    }

    private void initTimePickers() {
        WorkTimeTracerManager workTimeTracerManager = ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager();
        Time startTime = workTimeTracerManager.getStartTimeHolder().getTime();
        Time stopTime = workTimeTracerManager.getStopTimeHolder().getTime();

        boolean is24h = DateFormat.is24HourFormat(getContext());
        initTimePicker(startTimePicker, is24h, startTime);
        initTimePicker(stopTimePicker, is24h, stopTime);
    }

    private void initTimePicker(NonScrollableTimePicker timePicker, boolean is24h, Time time) {
        timePicker.setIs24HourView(is24h);
        timePicker.setOnTimeChangedListener(onTimeChangedListener);
        timePicker.setHour(time.getHours());
        timePicker.setMinute(time.getMinutes());
    }


    private void initOverHoursText() {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener
                (preferenceChangeListener);
        updateOverHoursText(((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager().getOverHours());
    }

    private void updateOverHoursText(Minutes minutes) {
        overHours.setText("Over hours: " + DateUtils.minutesToText(minutes));
    }

    private void initDiffText() {
        final MinutesHolder diffHolder = ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager().getDiffHolder();
        diffHolder.addObserver(diffHolderObserver);

        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(workTimePreferenceChangeListenerForDiff);
        updateDiffText(diffHolder.getMinutes());
    }

    private void updateDiffText(Minutes diff) {
        Minutes workTime =((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager().getNormalWorkHours();

        Minutes workTimeDiff = diff.minus(workTime);
        String verboseDiff = " (" + (workTimeDiff.isGreaterThan(Minutes.ZERO) ? "+" : "") + DateUtils.minutesToText(workTimeDiff) + ")";
        diffText.setText("Diff: " + DateUtils.minutesToText(diff) + verboseDiff);
    }

    @OnClick(R.id.startNow)
    public void startNow(View view) {
        now(startTimePicker);
    }

    @OnClick(R.id.stopNow)
    public void stopNow(View view) {
        now(stopTimePicker);
    }

    public void now(NonScrollableTimePicker timePicker) {
        DateTime now = new DateTime();
        timePicker.setHour(now.getHourOfDay());
        timePicker.setMinute(now.getMinuteOfHour());
    }

    @OnClick(R.id.log)
    public void log(View view) {
        WorkTimeTracerManager workTimeTracerManager = ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager();
        Minutes diff = workTimeTracerManager.getDiffHolder().getMinutes();
        Minutes todayOverHours = diff.minus(workTimeTracerManager.getNormalWorkHours());
        Minutes totalOverHours = workTimeTracerManager.getOverHours();
        Minutes newOverHours = totalOverHours.plus(todayOverHours);
        workTimeTracerManager.saveOverHours(newOverHours);
        // android M - request permission done in main activity
        Vibrator vibe = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(50); // 50 is time in ms
    }

    @Override
    public void onPause() {
        super.onPause();
        saveTimePickerValues();
    }

    private void saveTimePickerValues() {
        Time startTime = new Time(startTimePicker.getHour(), startTimePicker.getMinute());
        Time stopTime = new Time(stopTimePicker.getHour(), stopTimePicker.getMinute());
        ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager().saveStartStopTime(startTime, stopTime);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(workTimePreferenceChangeListenerForDiff);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        final MinutesHolder diffHolder = ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager().getDiffHolder();
        diffHolder.deleteObserver(diffHolderObserver);
    }
}
