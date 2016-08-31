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
import butterknife.Unbinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class WorkTimeTrackerFragment extends Fragment {

    private static final String TAG = WorkTimeTracker.class.getCanonicalName();

    private static final String START_HOUR = "START_HOUR";
    private static final String START_MINS = "START_MINS";
    private static final String STOP_HOUR = "STOP_HOUR";
    private static final String STOP_MINS = "STOP_MINS";

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

    public WorkTimeTrackerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_time_tracker, container, false);
        ButterKnife.bind(this, view);
        onTimeChangedListener = new OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Time startTime = new Time(startTimePicker.getHour(), startTimePicker.getMinute());
                Time stopTime = new Time(stopTimePicker.getHour(), stopTimePicker.getMinute());
                Minutes diff = DateUtils.diff(startTime, stopTime);
                ((WorkTimeTrackerApp) getActivity().getApplication()).getDiffHolder().setMinutes(diff);
            }
        };

        initTimePickers();
        initOverHoursText();
        initDiffText();
        logButton.setHapticFeedbackEnabled(true);
        return view;
    }

    private void initTimePickers() {
        DateTime currentTime = new DateTime();
        int currentHourOfDay = currentTime.getHourOfDay();
        int currentMinuteOfHour = currentTime.getMinuteOfHour();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int startHour = preferences.getInt(START_HOUR, currentHourOfDay);
        int startMins = preferences.getInt(START_MINS, currentMinuteOfHour);
        int stopHour = preferences.getInt(STOP_HOUR, currentHourOfDay);
        int stopMins = preferences.getInt(STOP_MINS, currentMinuteOfHour);
        boolean is24h = DateFormat.is24HourFormat(getContext());
        initTimePicker(startTimePicker, is24h, startHour, startMins);
        initTimePicker(stopTimePicker, is24h, stopHour, stopMins);
    }

    private void initTimePicker(NonScrollableTimePicker timePicker, boolean is24h, int hour, int mins) {
        timePicker.setIs24HourView(is24h);
        timePicker.setOnTimeChangedListener(onTimeChangedListener);
        timePicker.setHour(hour);
        timePicker.setMinute(mins);
    }

    // to keep it from GC
    // http://stackoverflow.com/questions/2542938/sharedpreferences-onsharedpreferencechangelistener-not-being-called
    // -consistently
    private final SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences
            .OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(WorkTimeTrackerApp.TOTAL_OVER_HOURS_AS_MINUTES)) {
                Log.d(TAG, key + " changed");
                Minutes minutes = Minutes.minutes(sharedPreferences.getInt(key, 0));
                updateOverHoursText(minutes);
            }
        }
    };

    private void initOverHoursText() {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener
                (preferenceChangeListener);
        updateOverHoursText(((WorkTimeTrackerApp) getActivity().getApplication()).getOverHours());
    }

    private void updateOverHoursText(Minutes minutes) {
        overHours.setText("Over hours: " + DateUtils.minutesToText(minutes));
    }

    private void initDiffText() {
        MinutesHolder diffHolder = ((WorkTimeTrackerApp) getActivity().getApplication()).getDiffHolder();
        diffHolder.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                updateDiffText((Minutes) data);
            }
        });
        updateDiffText(diffHolder.getMinutes());
    }

    private void updateDiffText(Minutes diff) {
        diffText.setText("Diff: " + DateUtils.minutesToText(diff));
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
        WorkTimeTrackerApp app = ((WorkTimeTrackerApp) getActivity().getApplication());
        Minutes diff = app.getDiffHolder().getMinutes();
        Minutes todayOverHours = diff.minus(app.getNormalWorkHours());
        Minutes totalOverHours = app.getOverHours();
        Minutes newOverHours = totalOverHours.plus(todayOverHours);
        app.saveOverHours(newOverHours);
        // android M - request permission done in main activity
        Vibrator vibe = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(50); // 50 is time in ms
    }

    @Override
    public void onPause() {
        super.onStop();
        saveTimePickerValues();
    }

    private void saveTimePickerValues() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putInt(START_HOUR, startTimePicker.getHour());
        editor.putInt(START_MINS, startTimePicker.getMinute());
        editor.putInt(STOP_HOUR, stopTimePicker.getHour());
        editor.putInt(STOP_MINS, stopTimePicker.getMinute());
        editor.apply();
    }
}
