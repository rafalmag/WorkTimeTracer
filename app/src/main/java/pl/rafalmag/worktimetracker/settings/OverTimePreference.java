package pl.rafalmag.worktimetracker.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.IBinder;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.rafalmag.worktimetracker.R;


/**
 * Inspired by https://github.com/commonsguy/cw-lunchlist/blob/master/19-Alarm/LunchList/src/apt/tutorial
 * /TimePreference.java
 */
public class OverTimePreference extends DialogPreference {

    private static final String TAG = OverTimePreference.class.getCanonicalName();

    private Unbinder unbinder;

    private boolean overTime = true;
    private int lastHour = 0;
    private int lastMinute = 0;
    @BindView(R.id.hourPicker)
    NumberPicker hourPicker;
    @BindView(R.id.minutePicker)
    NumberPicker minutePicker;
    @BindView(R.id.overUnderRadio)
    RadioGroup radioGroup;

    public OverTimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
        setPositiveButtonText(ctxt.getString(R.string.date_time_set));
        setNegativeButtonText(ctxt.getString(android.R.string.cancel));
        setDialogLayoutResource(R.layout.overtime);
    }

    /*
     * Inspired by
     * http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.2
     * .2_r1/android/widget/TimePicker.java#TimePicker
     */
    private void updateInputState() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context
                .INPUT_METHOD_SERVICE);
        IBinder windowToken = getDialog().getWindow().getDecorView().getWindowToken();
        // Make sure that if the user changes the value and the IME is active
        // for one of the inputs if this widget, the IME is closed. If the user
        // changed the value via the IME and there is a next input the IME will
        // be shown, otherwise the user chose another means of changing the
        // value and having the IME up makes no sense.
//        InputMethodManager inputMethodManager = InputMethodManager.peekInstance();
        if (inputMethodManager != null) {
            Log.d(TAG, "IMM not null");
            hourPicker.clearFocus();
            minutePicker.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    protected void onBindDialogView(View v) {
        unbinder = ButterKnife.bind(this, v);
        super.onBindDialogView(v);

        initHourPicker();
        initMinutePicker();
        initOverUnderTimeButtons();
    }

    private void initHourPicker() {
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateInputState();
            }
        });
        hourPicker.setValue(lastHour);
    }

    private void initMinutePicker() {
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateInputState();
                if (oldVal == 59 && newVal == 0) {
                    int oldHour = hourPicker.getValue();
                    hourPicker.setValue(oldHour + 1);
                }
                if (oldVal == 0 && newVal == 59) {
                    int oldHour = hourPicker.getValue();
                    if (oldHour >= 1) {
                        hourPicker.setValue(oldHour - 1);
                    }
                }
            }
        });
        minutePicker.setValue(lastMinute);
    }

    private void initOverUnderTimeButtons() {
        if (overTime) {
            radioGroup.clearCheck();
            radioGroup.check(R.id.overTimeButton);
        } else {
            radioGroup.clearCheck();
            radioGroup.check(R.id.underTimeButton);
        }
    }

    @OnClick(R.id.overTimeButton)
    protected void overTimeButtonOnClickListener() {
        overTime = true;
    }

    @OnClick(R.id.underTimeButton)
    protected void underTimeButtonOnClickListener() {
        overTime = false;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            lastHour = hourPicker.getValue();
            lastMinute = minutePicker.getValue();
            int mins = lastHour * 60 + lastMinute;
            if (!overTime) {
                mins = -mins;
            }
            if (callChangeListener(mins)) {
                Log.d(TAG, "Persisting " + getKey() + " = " + mins + " mins");
                persistInt(mins);
            }
        }
        unbinder.unbind();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        int defaultMins = getDefaultMins(defaultValue);
        int mins = getMins(restoreValue, defaultMins);
        overTime = mins > 0;
        lastHour = Math.abs(mins) / 60;
        lastMinute = Math.abs(mins) - 60 * lastHour;
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
