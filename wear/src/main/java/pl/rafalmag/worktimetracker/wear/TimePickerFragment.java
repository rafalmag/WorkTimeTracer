package pl.rafalmag.worktimetracker.wear;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.rafalmag.worktimetracerlibrary.CompatibleTimePicker;
import pl.rafalmag.worktimetracerlibrary.Time;
import pl.rafalmag.worktimetracerlibrary.WorkTimeTracerManager;

public class TimePickerFragment extends Fragment implements FragmentLifecycle {
    private static final String TAG = TimePickerFragment.class.getCanonicalName();

    public enum Mode {
        START("Start") {
            @Override
            public Time getTime(WorkTimeTracerManager workTimeTracerManager) {
                return workTimeTracerManager.getStartTime();
            }

            @Override
            public void saveTime(WorkTimeTracerManager workTimeTracerManager, Time startTime) {
                Time stopTime = workTimeTracerManager.getStopTime();
                workTimeTracerManager.saveStartStopTime(startTime, stopTime);
            }
        },
        STOP("Stop") {
            @Override
            public Time getTime(WorkTimeTracerManager workTimeTracerManager) {
                return workTimeTracerManager.getStopTime();
            }

            @Override
            public void saveTime(WorkTimeTracerManager workTimeTracerManager, Time stopTime) {
                Time startTime = workTimeTracerManager.getStartTime();
                workTimeTracerManager.saveStartStopTime(startTime, stopTime);
            }
        };

        final String text;

        Mode(String text) {
            //TODO text id - to provide i18n
            this.text = text;
        }

        public abstract Time getTime(WorkTimeTracerManager workTimeTracerManager);

        public String getText() {
            return text;
        }

        public abstract void saveTime(WorkTimeTracerManager workTimeTracerManager, Time time);

    }

    public static TimePickerFragment create(Mode mode) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.mode = mode;
        return timePickerFragment;

    }

    private Mode mode;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.timePicker)
    CompatibleTimePicker timePicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timepicker, container, false);
        ButterKnife.bind(this, view);

        title.setText(mode.getText());
        boolean is24h = DateFormat.is24HourFormat(getActivity());
        timePicker.setIs24HourView(is24h);
        //        timePicker.setOnTimeChangedListener(onTimeChangedListener);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume=" + this.getClass().getSimpleName() + " mode=" + mode + " calling onResumeFragment");
        onResumeFragment(getActivity());
    }

    @Override
    public void onResumeFragment(Activity activity) {
        Log.v(TAG, "onResumeFragment=" + this.getClass().getSimpleName() + " mode=" + mode);
        WorkTimeTracerManager workTimeTracerManager = ((WorkTimeTrackerApp) activity.getApplication()).getWorkTimeTracerManager();
        Time time = mode.getTime(workTimeTracerManager);
        timePicker.setHour(time.getHours());
        timePicker.setMinute(time.getMinutes());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause=" + this.getClass().getSimpleName() + " mode=" + mode + " calling onPauseFragment");
        onPauseFragment(getActivity());
    }

    @Override
    public void onPauseFragment(Activity activity) {
        Log.v(TAG, "onPauseFragment=" + this.getClass().getSimpleName() + " mode=" + mode);
        saveTimePickerValues(activity);
    }

    private void saveTimePickerValues(Activity activity) {
        Time time = new Time(timePicker.getHour(), timePicker.getMinute());
        WorkTimeTracerManager workTimeTracerManager = ((WorkTimeTrackerApp) activity.getApplication()).getWorkTimeTracerManager();
        mode.saveTime(workTimeTracerManager, time);
    }

    @OnClick(R.id.nowButton)
    public void startNow(View view) {
        DateTime now = new DateTime();
        timePicker.setHour(now.getHourOfDay());
        timePicker.setMinute(now.getMinuteOfHour());
    }

}
