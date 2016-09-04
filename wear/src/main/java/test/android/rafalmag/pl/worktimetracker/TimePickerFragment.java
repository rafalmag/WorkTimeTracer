package test.android.rafalmag.pl.worktimetracker;

import android.app.Fragment;
import android.media.tv.TvView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimePickerFragment extends Fragment {

    public enum Mode{
        START("Start"),
        STOP("Stop");

        final String text;
        Mode(String text) {
            //TODO text id - to provide i18n
            this.text=text;
        }


        public String getText() {
            return text;
        }
    }

    public static TimePickerFragment create(Mode mode){
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
        timePicker.setIs24HourView(true);

        return view;
    }

    @OnClick(R.id.nowButton)
    public void startNow(View view) {
        DateTime now = new DateTime();
        timePicker.setHour(now.getHourOfDay());
        timePicker.setMinute(now.getMinuteOfHour());
    }

}
