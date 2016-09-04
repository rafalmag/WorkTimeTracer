package test.android.rafalmag.pl.worktimetracker;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LogFragment extends Fragment {

    @BindView(R.id.delayed_confirm)
    DelayedConfirmationView mDelayedView;

    // icon from https://design.google.com/icons/
    // https://books.google.pl/books?id=OOkmCAAAQBAJ&pg=PA148&lpg=PA148&dq=android+wear+confirmation+example&source=bl&ots=USfV9lASYl&sig=agiOXpu8T5LesYidZ6SHfuBQFZ0&hl=en&sa=X&ved=0ahUKEwiF5ZrosvXOAhXsHJoKHdWOCXoQ6AEIUTAJ#v=onepage&q=android%20wear%20confirmation%20example&f=false


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log, container, false);
        ButterKnife.bind(this, view);

        // todo startActivityForResult() will give a callback. (dohh xD) Just override onActivityResult(int requestCode, int resultCode, Intent data)
        mDelayedView.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
            @Override
            public void onTimerFinished(View view) {
                running=false;
                // User didn't cancel, perform the action
                Intent intent = new Intent(getActivity(), ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.time_logged));
                startActivity(intent);
                mDelayedView.reset();
                //TODO go to first page
            }

            boolean running = false;

            @Override
            public void onTimerSelected(View view) {
                if(running) {
                    // User canceled, abort the action
                    mDelayedView.reset();
                    running = false;
                }else {
                    mDelayedView.setTotalTimeMs(2000);
                    // Start the timer
                    mDelayedView.start();
                    running = true;
                }
            }
        });
        return view;
    }

}
