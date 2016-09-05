package pl.rafalmag.worktimetracker.wear;


import android.app.Fragment;
import android.os.Bundle;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main, container, false);
        ButterKnife.bind(this, view);
        WorkTimeTracerManager workTimeTracerManager = ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager();
        Observer observer = new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                updateText();
            }
        };
        workTimeTracerManager.getStartTimeHolder().addObserver(observer);
        workTimeTracerManager.getStopTimeHolder().addObserver(observer);
        workTimeTracerManager.getDiffHolder().addObserver(observer);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateText();
    }

    private void updateText() {
        WorkTimeTracerManager workTimeTracerManager = ((WorkTimeTrackerApp) getActivity().getApplication()).getWorkTimeTracerManager();

        Minutes overHours = workTimeTracerManager.getOverHours();
        // on wear emulator the short time formatter for pl_PL contains AM/PM - on real watch it is ok
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        String startTime = timeFormat.format(workTimeTracerManager.getStartTimeHolder().getTime().toDate());
        String stopTime = timeFormat.format(workTimeTracerManager.getStopTimeHolder().getTime().toDate());
        Minutes diff = workTimeTracerManager.getDiffHolder().getMinutes();
        String text = "Over hours\n" + DateUtils.minutesToText(overHours) + "\nStart " + startTime + "\nStop " + stopTime + "\nDiff: " + DateUtils.minutesToText(diff);
        mainText.setText(text);
    }
}
