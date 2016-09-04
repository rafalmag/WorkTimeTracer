package pl.rafalmag.worktimetracker.wear;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;

public class MainPagerAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;

    public MainPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;
    }

    @Override
    public Fragment getFragment(int row, int col) {
        switch (col){
            case 0:
                return new MainFragment();
            case 1:
                return TimePickerFragment.create(TimePickerFragment.Mode.START);
            case 2:
                return TimePickerFragment.create(TimePickerFragment.Mode.STOP);
            case 3:
                return new LogFragment();
            default:
                String title = "Row " + row + " col " + col;
                return CardFragment.create(title, title);
        }
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