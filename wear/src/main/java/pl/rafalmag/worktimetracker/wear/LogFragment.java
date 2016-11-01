package pl.rafalmag.worktimetracker.wear;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.Minutes;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.rafalmag.worktimetracerlibrary.PersistenceManager;
import pl.rafalmag.worktimetracerlibrary.WorkTimeTracerManager;


public class LogFragment extends Fragment {

    private static final String TAG = LogFragment.class.getCanonicalName();
    public static final int LOG_CONFIRMATION_REQUEST_CODE = 0;

    public static LogFragment create(GridViewPager gridViewPager) {
        LogFragment logFragment = new LogFragment();
        logFragment.setGridViewPager(gridViewPager);
        return logFragment;
    }

    @BindView(R.id.delayed_confirm)
    DelayedConfirmationView mDelayedView;

    // icon from https://design.google.com/icons/
    // https://books.google.pl/books?id=OOkmCAAAQBAJ&pg=PA148&lpg=PA148&dq=android+wear+confirmation+example&source=bl&ots=USfV9lASYl&sig=agiOXpu8T5LesYidZ6SHfuBQFZ0&hl=en&sa=X&ved=0ahUKEwiF5ZrosvXOAhXsHJoKHdWOCXoQ6AEIUTAJ#v=onepage&q=android%20wear%20confirmation%20example&f=false
    boolean mDelayedViewRunning = false;
    private GridViewPager gridViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log, container, false);
        ButterKnife.bind(this, view);

        mDelayedView.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
            @Override
            public void onTimerFinished(View view) {
                if (mDelayedViewRunning) {
                    // User didn't cancel, perform the action
                    WorkTimeTrackerApp workTimeTrackerApp = (WorkTimeTrackerApp) getActivity().getApplication();
                    WorkTimeTracerManager workTimeTracerManager = workTimeTrackerApp.getWorkTimeTracerManager();
                    PersistenceManager persistenceManager = workTimeTrackerApp.getPersistenceManager();
                    Minutes diff = workTimeTracerManager.getDiffHolder().getMinutes();
                    Minutes todayOvertime = diff.minus(persistenceManager.getWorkTime());
                    Minutes totalOvertime = persistenceManager.getOvertime();
                    Minutes newOvertime = totalOvertime.plus(todayOvertime);
                    persistenceManager.saveOvertime(newOvertime);
                    workTimeTracerManager.getOvertimeHolder().setMinutes(newOvertime);

                    // startActivityForResult displays ConfirmationActivity, and result from this will be passed to onActivityResult
                    Intent intent = new Intent(getActivity(), ConfirmationActivity.class);
                    intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
                    intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.time_logged));
                    startActivityForResult(intent, LOG_CONFIRMATION_REQUEST_CODE);
                } else {
                    Log.d(TAG, "timer finished, but delayedView is not running - can happen when user scrolls out = cancel");
                }
                mDelayedView.reset();
                mDelayedViewRunning = false;
            }

            @Override
            public void onTimerSelected(View view) {
                if (mDelayedViewRunning) {
                    // User canceled, abort the action
                    mDelayedView.reset();
                    mDelayedViewRunning = false;
                } else {
                    mDelayedView.setTotalTimeMs(2000);
                    // Start the timer
                    mDelayedView.start();
                    mDelayedViewRunning = true;
                }
            }
        });
        return view;
    }

    // result from ConfirmationActivity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOG_CONFIRMATION_REQUEST_CODE) {
            //go to first page
            gridViewPager.setCurrentItem(0, 0, false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDelayedViewRunning = false;
        mDelayedView.reset();
    }

    public void setGridViewPager(GridViewPager gridViewPager) {
        this.gridViewPager = gridViewPager;
    }

    public GridViewPager getGridViewPager() {
        return gridViewPager;
    }
}
