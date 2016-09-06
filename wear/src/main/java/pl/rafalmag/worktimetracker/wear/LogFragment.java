package pl.rafalmag.worktimetracker.wear;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LogFragment extends Fragment {

    private static final String TAG = LogFragment.class.getCanonicalName();

    @BindView(R.id.delayed_confirm)
    DelayedConfirmationView mDelayedView;

    // icon from https://design.google.com/icons/
    // https://books.google.pl/books?id=OOkmCAAAQBAJ&pg=PA148&lpg=PA148&dq=android+wear+confirmation+example&source=bl&ots=USfV9lASYl&sig=agiOXpu8T5LesYidZ6SHfuBQFZ0&hl=en&sa=X&ved=0ahUKEwiF5ZrosvXOAhXsHJoKHdWOCXoQ6AEIUTAJ#v=onepage&q=android%20wear%20confirmation%20example&f=false
    boolean mDelayedViewRunning = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log, container, false);
        ButterKnife.bind(this, view);

        // todo startActivityForResult() will give a callback. (dohh xD) Just override onActivityResult(int requestCode, int resultCode, Intent data)
        mDelayedView.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
            @Override
            public void onTimerFinished(View view) {
                if(mDelayedViewRunning) {
                    // User didn't cancel, perform the action
                    Intent intent = new Intent(getActivity(), ConfirmationActivity.class);
                    intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
                    intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.time_logged));
                    startActivity(intent);
                    //TODO go to first page
                }else {
                    Log.d(TAG,"timer finished, but delayedView is not running - can happen when user scrolls out = cancel");
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

    @Override
    public void onPause() {
        super.onPause();
        mDelayedViewRunning = false;
        mDelayedView.reset();
    }
}
