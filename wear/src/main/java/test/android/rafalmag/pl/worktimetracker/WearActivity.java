package test.android.rafalmag.pl.worktimetracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.BoxInsetLayout;
import android.widget.TimePicker;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WearActivity extends Activity {

    @BindView(R.id.timePicker)
    TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        ButterKnife.bind(this);
//        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
//        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
//            @Override
//            public void onLayoutInflated(WatchViewStub stub) {
//                mTextView = (TextView) stub.findViewById(R.id.text);
//            }
//        });
        timePicker.setIs24HourView(true);
    }

}
