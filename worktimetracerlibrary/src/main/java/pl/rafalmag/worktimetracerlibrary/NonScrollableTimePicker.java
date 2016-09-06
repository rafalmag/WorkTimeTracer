package pl.rafalmag.worktimetracerlibrary;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

public class NonScrollableTimePicker extends CompatibleTimePicker {
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


}
