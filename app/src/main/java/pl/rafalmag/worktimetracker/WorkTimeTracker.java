package pl.rafalmag.worktimetracker;

import android.app.Activity;
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

    @Override
    protected void onStop() {
        super.onStop();
        ((WorkTimeTrackerApp) getApplication()).saveOverHours();
    }

    private void initTimePickers() {
        boolean is24h = is24h();
        initTimePicker((TimePicker) findViewById(R.id.startTimePicker), is24h);
        initTimePicker((TimePicker) findViewById(R.id.stopTimePicker), is24h);
    }

    private void initTimePicker(TimePicker timePicker, boolean is24h) {
        timePicker.setIs24HourView(is24h);
        timePicker.setOnTimeChangedListener(onTimeChangedListener);
    }

    private boolean is24h() {
        String timeS = Settings.System.getString(getContentResolver(),
                Settings.System.TIME_12_24);
        return timeS == null || timeS.equals("24");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.work_time_tracker, menu);
        return true;
    }

}
