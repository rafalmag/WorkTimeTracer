package pl.rafalmag.worktimetracker.wear;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

public class MainPagerAdapter extends FragmentGridPagerAdapter /*implements GridViewPager.OnPageChangeListener*/ {

    private static final String TAG = MainPagerAdapter.class.getCanonicalName();
    private final Activity activity;

    private final List<Fragment> fragments = Arrays.<Fragment>asList(
            new MainFragment(),
            TimePickerFragment.create(TimePickerFragment.Mode.START),
            TimePickerFragment.create(TimePickerFragment.Mode.STOP),
            new LogFragment());

    public MainPagerAdapter(Activity activity, FragmentManager fm) {
        super(fm);
        this.activity = activity;
    }

    @Override
    public Fragment getFragment(int row, int col) {
       return fragments.get(col);
    }

    // Obtain the number of pages (vertical)
    @Override
    public int getRowCount() {
        return 1;
    }

    // Obtain the number of pages (horizontal)
    @Override
    public int getColumnCount(int rowNum) {
        return 4;
    }

//    @Override
//    public void finishUpdate(ViewGroup container) {
//        super.finishUpdate(container);
//        Log.v(TAG, "finishUpdate");
//
//        Fragment fragmentToShow = getFragment(currentRow, currentColumn);
//        Log.v(TAG, "fragmentToShow (row=" + currentRow + " col=" + currentColumn + "):" + fragmentToShow.getClass().getSimpleName());
////                if (fragmentToShow.isResumed()) {
//        ((FragmentLifecycle) fragmentToShow).onResumeFragment(activity);
////                }
//
//        Fragment fragmentToHide = getFragment(oldRow, oldColumn);
//        Log.v(TAG, "fragmentToHide (row=" + oldRow + " col=" + oldColumn + "):" + fragmentToHide.getClass().getSimpleName());
////                if (fragmentToHide.isResumed()) {
//        ((FragmentLifecycle) fragmentToHide).onPauseFragment(activity);
////                }
//
//    }
//
//
//    int currentRow = 0;
//    int currentColumn = 0;
//    int oldRow = 0;
//    int oldColumn = 0;
//
//    @Override
//    public void onPageScrolled(int row, int column, float rowOffset, float columnOffset, int rowOffsetPixels, int columnOffsetPixels) {
//
//    }
//
//    @Override
//    public void onPageSelected(int row, int column) {
//        oldRow = currentRow;
//        oldColumn = currentColumn;
//        currentRow = row;
//        currentColumn = column;
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int state) {
//
//    }
}