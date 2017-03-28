package pl.rafalmag.worktimetracker.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import pl.rafalmag.worktimetracerlibrary.CompatibleTimePicker;
import pl.rafalmag.worktimetracker.R;

/**
 * User: rafalmag
 * Date: 14.04.13
 * Time: 19:58
 * <p/>
 * inspired by https://github.com/commonsguy/cw-lunchlist/blob/master/19-Alarm/LunchList/src/apt/tutorial
 * /TimePreference.java
 */

public class TimePreference extends DialogPreference {

    private static final String TAG = TimePreference.class.getCanonicalName();

    private int lastHour = 0;
    private int lastMinute = 0;
    private CompatibleTimePicker picker;

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
        setPositiveButtonText(ctxt.getString(R.string.date_time_set));
        setNegativeButtonText(ctxt.getString(android.R.string.cancel));
    }

    @Override
    protected View onCreateDialogView() {
        picker = new CompatibleTimePicker(getContext());
        picker.setIs24HourView(true);
        return picker;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setHour(lastHour);
        picker.setMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            lastHour = picker.getHour();
            lastMinute = picker.getMinute();
            int mins = lastHour * 60 + lastMinute;
            if (callChangeListener(mins)) {
                Log.d(TAG, "Persisting " + getKey() + " = " + mins + " mins");
                persistInt(mins);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        int defaultMins = getDefaultMins(defaultValue);
        int mins = getMins(restoreValue, defaultMins);

        lastHour = mins / 60;
        lastMinute = mins - 60 * lastHour;
    }

    private int getDefaultMins(Object defaultValue) {
        if (defaultValue == null) {
            return 0;
        }
        if (defaultValue instanceof String) {
            return Integer.parseInt((String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return (Integer) defaultValue;
        } else {
            throw new IllegalArgumentException("default value is not a string or int " + defaultValue);
        }
    }

    private int getMins(boolean restoreValue, int defaultMins) {
        if (restoreValue) {
            int mins = getPersistedInt(defaultMins);
            Log.d(TAG, "Loaded " + getKey() + "=" + mins + "mins");
            return mins;
        } else {
            return defaultMins;
        }
    }


}
