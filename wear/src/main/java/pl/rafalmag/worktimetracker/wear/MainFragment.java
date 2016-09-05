package pl.rafalmag.worktimetracker.wear;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.Minutes;

import java.text.DateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.rafalmag.worktimetracerlibrary.DateUtils;
import pl.rafalmag.worktimetracerlibrary.Time;
import pl.rafalmag.worktimetracerlibrary.WorkTimeTracerManager;

public class MainFragment extends Fragment implements FragmentLifecycle{

    private static final String TAG = MainFragment.class.getCanonicalName();
    @BindView(R.id.mainText)
    TextView mainText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume=" + this.getClass().getSimpleName() + " calling onResumeFragment");
        onResumeFragment(getActivity());
    }

    @Override
    public void onResumeFragment(Activity activity) {
        Log.v(TAG, "onResumeFragment=" + this.getClass().getSimpleName());
        WorkTimeTracerManager workTimeTracerManager = ((WorkTimeTrackerApp) activity.getApplication()).getWorkTimeTracerManager();

        Minutes overHours = workTimeTracerManager.getOverHours();
        // on wear emulator the short time formatter for pl_PL contains AM/PM - on real watch it is ok
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        Time startTime = workTimeTracerManager.getStartTime();
        Time stopTime = workTimeTracerManager.getStopTime();
        Minutes diff = DateUtils.diff(startTime, stopTime);
        String text =
                "Over hours\n" +
                        DateUtils.minutesToText(overHours) + "\n" +
                        "Start " + timeFormat.format(startTime.toDate()) + "\n" +
                        "Stop " + timeFormat.format(stopTime.toDate()) + "\n" +
                        "Diff: " + DateUtils.minutesToText(diff);
        mainText.setText(text);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause=" + this.getClass().getSimpleName());
    }

    @Override
    public void onPauseFragment(Activity activity) {
        Log.v(TAG, "onPauseFragment=" + this.getClass().getSimpleName());
    }
}
