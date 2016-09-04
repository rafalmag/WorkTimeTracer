package test.android.rafalmag.pl.worktimetracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WearActivity extends Activity {

    @BindView(R.id.pager)
    GridViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        ButterKnife.bind(this);
        pager.setAdapter(new MainPagerAdapter(this, getFragmentManager()));
    }

}
