package pl.rafalmag.worktimetracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import java.util.Observable;
import java.util.Observer;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

public class WorkTimeTracker extends Activity {

    private static final String TAG = WorkTimeTracker.class.getCanonicalName();

    private static final String START_HOUR = "START_HOUR";
    private static final String START_MINS = "START_MINS";
    private static final String STOP_HOUR = "STOP_HOUR";
    private static final String STOP_MINS = "STOP_MINS";

    private final OnTimeChangedListener onTimeChangedListener = new OnTimeChangedListener() {

        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            TimePicker startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
            Time startTime = new Time(startTimePicker.getCurrentHour(), startTimePicker.getCurrentMinute());
            TimePicker stopTimePicker = (TimePicker) findViewById(R.id.stopTimePicker);
            Time stopTime = new Time(stopTimePicker.getCurrentHour(), stopTimePicker.getCurrentMinute());
            Minutes diff = DateUtils.diff(startTime, stopTime);
            ((WorkTimeTrackerApp) getApplication()).getDiffHolder().setMinutes(diff);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_time_tracker);
        initTimePickers();
        initOverHoursText();
        initDiffText();
        Button logButton = (Button) findViewById(R.id.log);
        logButton.setHapticFeedbackEnabled(true);
    }

    private void initTimePickers() {
        DateTime currentTime = new DateTime();
        int currentHourOfDay = currentTime.getHourOfDay();
        int currentMinuteOfHour = currentTime.getMinuteOfHour();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        int startHour = preferences.getInt(START_HOUR, currentHourOfDay);
        int startMins = preferences.getInt(START_MINS, currentMinuteOfHour);
        int stopHour = preferences.getInt(STOP_HOUR, currentHourOfDay);
        int stopMins = preferences.getInt(STOP_MINS, currentMinuteOfHour);
        boolean is24h = is24h();
        initTimePicker((TimePicker) findViewById(R.id.startTimePicker), is24h, startHour, startMins);
        initTimePicker((TimePicker) findViewById(R.id.stopTimePicker), is24h, stopHour, stopMins);
    }

    private boolean is24h() {
        String timeS = Settings.System.getString(getContentResolver(),
                Settings.System.TIME_12_24);
        return timeS == null || timeS.equals("24");
    }

    private void initTimePicker(TimePicker timePicker, boolean is24h, int hour, int mins) {
        timePicker.setIs24HourView(is24h);
        timePicker.setOnTimeChangedListener(onTimeChangedListener);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(mins);
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
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener
                (preferenceChangeListener);
        updateOverHoursText(((WorkTimeTrackerApp) getApplication()).getOverHours());
    }

    private void updateOverHoursText(Minutes overHours) {
        TextView diffText = (TextView) findViewById(R.id.overHours);
        diffText.setText("Over hours: " + DateUtils.minutesToText(overHours));
    }

    private void initDiffText() {
        MinutesHolder diffHolder = ((WorkTimeTrackerApp) getApplication()).getDiffHolder();
        diffHolder.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                updateDiffText((Minutes) data);
            }
        });
        updateDiffText(diffHolder.getMinutes());
    }

    private void updateDiffText(Minutes diff) {
        TextView diffText = (TextView) findViewById(R.id.diff_time);
        diffText.setText("Diff: " + DateUtils.minutesToText(diff));
    }

    public void now(View view) {
        TimePicker timePicker = getTimePickerForNowButton(view);
        DateTime now = new DateTime();
        timePicker.setCurrentHour(now.getHourOfDay());
        timePicker.setCurrentMinute(now.getMinuteOfHour());
    }

    private TimePicker getTimePickerForNowButton(View view) {
        switch (view.getId()) {
            case R.id.startNow:
                return (TimePicker) findViewById(R.id.startTimePicker);
            case R.id.stopNow:
                return (TimePicker) findViewById(R.id.stopTimePicker);
            default:
                throw new IllegalStateException(view + " is neither start nor stop button.");
        }
    }

    public void log(View view) {
        WorkTimeTrackerApp app = ((WorkTimeTrackerApp) getApplication());
        Minutes diff = app.getDiffHolder().getMinutes();
        Minutes todayOverHours = diff.minus(app.getNormalWorkHours());
        Minutes totalOverHours = app.getOverHours();
        Minutes newOverHours = totalOverHours.plus(todayOverHours);
        app.saveOverHours(newOverHours);
        Vibrator vibe = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(50); // 50 is time in ms
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveTimePickerValues();
    }

    private void saveTimePickerValues() {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        TimePicker startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
        editor.putInt(START_HOUR, startTimePicker.getCurrentHour());
        editor.putInt(START_MINS, startTimePicker.getCurrentMinute());
        TimePicker stopTimePicker = (TimePicker) findViewById(R.id.stopTimePicker);
        editor.putInt(STOP_HOUR, stopTimePicker.getCurrentHour());
        editor.putInt(STOP_MINS, stopTimePicker.getCurrentMinute());
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.work_time_tracker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.d(TAG, "Menu action settings selected");
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
