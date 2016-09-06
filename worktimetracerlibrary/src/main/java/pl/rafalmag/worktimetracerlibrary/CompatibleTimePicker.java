package pl.rafalmag.worktimetracerlibrary;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TimePicker;

public class CompatibleTimePicker extends TimePicker {

    public CompatibleTimePicker(Context context) {
        super(context);
    }

    public CompatibleTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompatibleTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CompatibleTimePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
