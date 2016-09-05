package pl.rafalmag.worktimetracker.wear;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;

import java.util.Arrays;
import java.util.List;

public class MainPagerAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;

    public MainPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;
    }

    private final List<Fragment> fragments = Arrays.asList(
            new MainFragment(),
            TimePickerFragment.create(TimePickerFragment.Mode.START),
            TimePickerFragment.create(TimePickerFragment.Mode.STOP),
            new LogFragment());

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

}