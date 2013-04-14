package pl.rafalmag.worktimetracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
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

    private static final String TAG = WorkTimeTracker.class.getSimpleName();

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
        initNowButtons();
        initLogButton();
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
        initTimePicker((TimePicker) findViewById(R.id.startTimePicker), is24h,startHour,startMins);
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
    private void initOverHoursText() {
        MinutesHolder overHoursHolder = ((WorkTimeTrackerApp) getApplication()).getOverHoursHolder();
        overHoursHolder.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                updateOverHoursText((Minutes) data);
            }
        });
        updateOverHoursText(overHoursHolder.getMinutes());
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

    private void initNowButtons() {
        initNowButton((Button) findViewById(R.id.startNow), (TimePicker) findViewById(R.id.startTimePicker));
        initNowButton((Button) findViewById(R.id.stopNow), (TimePicker) findViewById(R.id.stopTimePicker));
    }


    private void initNowButton(Button button, final TimePicker timePicker) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime now = new DateTime();
                timePicker.setCurrentHour(now.getHourOfDay());
                timePicker.setCurrentMinute(now.getMinuteOfHour());
            }
        });
    }
    private void initLogButton() {
        Button logButton = (Button) findViewById(R.id.log);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkTimeTrackerApp app = ((WorkTimeTrackerApp) getApplication());
                Minutes diff = app.getDiffHolder().getMinutes();
                Minutes todayOverHours = diff.minus(app.getNormalWorkHours());
                Minutes totalOverHours = app.getOverHoursHolder().getMinutes();
                Minutes newOverHours = totalOverHours.plus(todayOverHours);
                app.getOverHoursHolder().setMinutes(newOverHours);
            }
        });
    }





    @Override
    protected void onStop() {
        super.onStop();
        ((WorkTimeTrackerApp) getApplication()).saveOverHours();
        saveTimePickerValues();
    }

    private void saveTimePickerValues() {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        TimePicker startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
        editor.putInt(START_HOUR,startTimePicker.getCurrentHour());
        editor.putInt(START_MINS,startTimePicker.getCurrentMinute());
        TimePicker stopTimePicker = (TimePicker) findViewById(R.id.stopTimePicker);
        editor.putInt(STOP_HOUR,stopTimePicker.getCurrentHour());
        editor.putInt(STOP_MINS,stopTimePicker.getCurrentMinute());
        editor.commit();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.work_time_tracker, menu);
        return true;
    }

}
