package pl.rafalmag.worktimetracker;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class WorkTimeTracker extends Activity {

	private static final String TAG = WorkTimeTracker.class.getSimpleName();


    private final OnTimeChangedListener onTimeChangedListener = new OnTimeChangedListener() {

        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            TimePicker startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
            TodayDate startDate = new TodayDate(startTimePicker.getCurrentHour(), startTimePicker.getCurrentMinute());
            TimePicker stopTimePicker = (TimePicker) findViewById(R.id.stopTimePicker);
            TodayDate stopDate = new TodayDate(stopTimePicker.getCurrentHour(), stopTimePicker.getCurrentMinute());
            String diff = DateUtils.diff(startDate, stopDate);

            TextView diffText = (TextView) findViewById(R.id.diff_time);
            diffText.setText("Diff: "+diff);
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_time_tracker);
		initTimePickers();
	}

	private void initTimePickers() {
		boolean is24h = is24h();
        initTimePicker((TimePicker) findViewById(R.id.startTimePicker),is24h);
        initTimePicker((TimePicker) findViewById(R.id.stopTimePicker),is24h);
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
