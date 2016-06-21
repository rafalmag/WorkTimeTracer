package pl.rafalmag.worktimetracker;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.TimePicker;

public class NonScrollableTimePicker extends TimePicker {
    public NonScrollableTimePicker(Context context) {
        super(context);
    }

    public NonScrollableTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonScrollableTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NonScrollableTimePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // copied from http://stackoverflow.com/a/14440893/252363, http://stackoverflow.com/a/9687545/252363
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Stop ScrollView from getting involved once you interact with the View
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            ViewParent p = getParent();
            if (p != null)
                p.requestDisallowInterceptTouchEvent(true);
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setHour(int hour) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setHour(hour);
        } else {
            super.setCurrentHour(hour);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getHour() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.getHour();
        } else {
            return super.getCurrentHour();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setMinute(int minute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setMinute(minute);
        } else {
            super.setCurrentMinute(minute);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getMinute() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.getMinute();
        } else {
            return super.getCurrentMinute();
        }
    }
}
