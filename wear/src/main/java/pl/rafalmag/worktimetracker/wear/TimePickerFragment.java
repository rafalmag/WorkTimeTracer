package pl.rafalmag.worktimetracker.wear;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.wearable.view.BoxInsetLayout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.rafalmag.worktimetracerlibrary.CompatibleTimePicker;
import pl.rafalmag.worktimetracerlibrary.Time;
import pl.rafalmag.worktimetracerlibrary.WorkTimeTracerManager;

public class TimePickerFragment extends Fragment {

    public enum Mode {
        START(R.string.start) {
            @Override
            public Time getTime(WorkTimeTracerManager workTimeTracerManager) {
                return workTimeTracerManager.getStartTimeHolder().getTime();
            }

            @Override
            public void saveTime(WorkTimeTracerManager workTimeTracerManager, Time startTime) {
                Time stopTime = workTimeTracerManager.getStopTimeHolder().getTime();
                workTimeTracerManager.saveStartStopTime(startTime, stopTime);
            }

            @Override
            public void setTime(WorkTimeTracerManager workTimeTracerManager, Time startTime) {
                workTimeTracerManager.getStartTimeHolder().setTime(startTime);
            }
        },
        STOP(R.string.stop) {
            @Override
            public Time getTime(WorkTimeTracerManager workTimeTracerManager) {
                return workTimeTracerManager.getStopTimeHolder().getTime();
            }

            @Override
            public void saveTime(WorkTimeTracerManager workTimeTracerManager, Time stopTime) {
                Time startTime = workTimeTracerManager.getStartTimeHolder().getTime();
                workTimeTracerManager.saveStartStopTime(startTime, stopTime);
            }

            @Override
            public void setTime(WorkTimeTracerManager workTimeTracerManager, Time stopTime) {
                workTimeTracerManager.getStopTimeHolder().setTime(stopTime);
            }
        };

        final int textId;

        Mode(int textId) {
            this.textId = textId;
        }

        public abstract Time getTime(WorkTimeTracerManager workTimeTracerManager);

        public int getTextId() {
            return textId;
        }

        public abstract void saveTime(WorkTimeTracerManager workTimeTracerManager, Time time);

        public abstract void setTime(WorkTimeTracerManager workTimeTracerManager, Time time);
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

    @BindView(R.id.timePickerBoxInsetLayout)
    BoxInsetLayout timePickerBoxInsetLayout;

    private TimePicker.OnTimeChangedListener onTimeChangedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timepicker, container, false);
        ButterKnife.bind(this, view);

        title.setText(getText(mode.getTextId()));

        ViewCompat.setOnApplyWindowInsetsListener(view, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                int systemWindowInsetBottom = insets.getSystemWindowInsetBottom();
                if (systemWindowInsetBottom > 0) {
                    // timePickerBoxInsetLayout changes bottom padding
                    timePickerBoxInsetLayout.setPadding(0, timePickerBoxInsetLayout.getPaddingTop(), 0, systemWindowInsetBottom);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) timePicker.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, 0, layoutParams.rightMargin, 0);
                }
                return insets;
            }
        });

        initTimePicker();
        return view;
    }

    private void initTimePicker() {
        final WorkTimeTracerManager workTimeTracerManager = ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager();
        Time time = mode.getTime(workTimeTracerManager);

        boolean is24h = DateFormat.is24HourFormat(getActivity());
        timePicker.setIs24HourView(is24h);
        onTimeChangedListener = new TimePicker.OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Time time = new Time(timePicker.getHour(), timePicker.getMinute());
                mode.setTime(workTimeTracerManager, time);
            }
        };
        timePicker.setOnTimeChangedListener(onTimeChangedListener);
        timePicker.setHour(time.getHours());
        timePicker.setMinute(time.getMinutes());
    }

    @Override
    public void onPause() {
        super.onPause();
        saveTimePickerValues();
    }

    private void saveTimePickerValues() {
        Time time = new Time(timePicker.getHour(), timePicker.getMinute());
        WorkTimeTracerManager workTimeTracerManager = ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager();
        mode.saveTime(workTimeTracerManager, time);
    }

    @OnClick(R.id.nowButton)
    public void startNow(View view) {
        DateTime now = new DateTime();
        timePicker.setHour(now.getHourOfDay());
        timePicker.setMinute(now.getMinuteOfHour());
    }

}
