package pl.rafalmag.worktimetracker.wear;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.Minutes;

import java.text.DateFormat;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.rafalmag.worktimetracerlibrary.DateUtils;
import pl.rafalmag.worktimetracerlibrary.WorkTimeTracerManager;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getCanonicalName();
    @BindView(R.id.mainText)
    TextView mainText;
    private Observer observer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main, container, false);
        ButterKnife.bind(this, view);

        observer = new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                updateMainText();
            }
        };
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        WorkTimeTracerManager workTimeTracerManager = ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager();
        workTimeTracerManager.getStartTimeHolder().addObserver(observer);
        workTimeTracerManager.getStopTimeHolder().addObserver(observer);
        workTimeTracerManager.getDiffHolder().addObserver(observer);
        updateMainText();
    }

    @Override
    public void onPause() {
        super.onPause();
        WorkTimeTracerManager workTimeTracerManager = ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager();
        workTimeTracerManager.getStartTimeHolder().deleteObserver(observer);
        workTimeTracerManager.getStopTimeHolder().deleteObserver(observer);
        workTimeTracerManager.getDiffHolder().deleteObserver(observer);
    }

    private void updateMainText() {
        WorkTimeTracerManager workTimeTracerManager = ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager();

        Minutes overtime = workTimeTracerManager.getOvertimeHolder().getMinutes();
        // on wear emulator the short time formatter for pl_PL contains AM/PM - on real watch it is ok
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        String startTime = timeFormat.format(workTimeTracerManager.getStartTimeHolder().getTime().toDate());
        String stopTime = timeFormat.format(workTimeTracerManager.getStopTimeHolder().getTime().toDate());
        Minutes diff = workTimeTracerManager.getDiffHolder().getMinutes();
        String verboseDiff = getVerboseDiff(diff);
        // TODO localize this
        String text = "Overtime\n" +
                DateUtils.minutesToText(overtime) +
                "\nStart " + startTime + "\n" +
                "Stop " + stopTime + "\n" +
                "Diff: " + DateUtils.minutesToText(diff) + "\n" +
                verboseDiff;
        mainText.setText(text);
    }

    @NonNull
    private String getVerboseDiff(Minutes diff) {
        Minutes workTime = ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager().getWorkTimeHolder().getMinutes();
        Minutes workTimeDiff = diff.minus(workTime);
        return " (" + (workTimeDiff.isGreaterThan(Minutes.ZERO) ? "+" : "") + DateUtils.minutesToText(workTimeDiff) + ")";
    }
}
