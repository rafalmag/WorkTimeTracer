package pl.rafalmag.worktimetracker.wear;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WearActivity extends Activity {

    private static final String TAG = WearActivity.class.getCanonicalName();

    @BindView(R.id.pager)
    GridViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        ButterKnife.bind(this);
        final MainPagerAdapter pageAdapter = new MainPagerAdapter(this, getFragmentManager());
        pager.setAdapter(pageAdapter);
//        pager.setOnPageChangeListener(pageAdapter);
        // based on https://looksok.wordpress.com/2013/11/02/viewpager-with-detailed-fragment-lifecycle-onresumefragment-including-source-code/
        pager.setOnPageChangeListener(new GridViewPager.OnPageChangeListener() {
            int currentRow = 0;
            int currentColumn = 0;

            @Override
            public void onPageScrolled(int row, int column, float rowOffset, float columnOffset, int rowOffsetPixels, int columnOffsetPixels) {

            }

            @Override
            public void onPageSelected(int row, int column) {
                Fragment fragmentToHide = pageAdapter.getFragment(currentRow, currentColumn);
                Log.v(TAG, "fragmentToHide (row=" + currentRow + " col=" + currentColumn + "):" + fragmentToHide.getClass().getSimpleName());
                ((FragmentLifecycle) fragmentToHide).onPauseFragment(WearActivity.this);

                Fragment fragmentToShow = pageAdapter.getFragment(row, column);
                Log.v(TAG, "fragmentToShow (row=" + row + " col=" + column + "):" + fragmentToShow.getClass().getSimpleName());
                ((FragmentLifecycle) fragmentToShow).onResumeFragment(WearActivity.this);


                currentRow = row;
                currentColumn = column;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
