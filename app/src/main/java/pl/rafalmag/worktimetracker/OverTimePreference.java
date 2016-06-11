package pl.rafalmag.worktimetracker;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.IBinder;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


/**
 * Inspired by https://github.com/commonsguy/cw-lunchlist/blob/master/19-Alarm/LunchList/src/apt/tutorial
 * /TimePreference.java
 */

public class OverTimePreference extends DialogPreference {

    private static final String TAG = OverTimePreference.class.getCanonicalName();

    private boolean overTime = true;
    private int lastHour = 0;
    private int lastMinute = 0;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    public static final int OVER_TIME_BUTTON_ID = 1;
    public static final int UNDER_TIME_BUTTON_ID = 2;
    private RadioGroup radioGroup;

    public OverTimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        try {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL));
            View unlimitedTimePicker = initUnlimitedTimePicker();
            linearLayout.addView(unlimitedTimePicker, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER_HORIZONTAL));
            RadioGroup radioGroup = initRadioGroup();
            linearLayout.addView(radioGroup, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER_HORIZONTAL));
            return linearLayout;
        } catch (Exception e) {
            Log.e(TAG, "onCreateDialogView", e);
            return null;
        }
    }

    private View initUnlimitedTimePicker() {
        hourPicker = new NumberPicker(getContext());
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(Integer.MAX_VALUE);
        hourPicker.setWrapSelectorWheel(false);
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateInputState();
            }
        });
        hourPicker.setFocusable(false);

        minutePicker = new NumberPicker(getContext());
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setWrapSelectorWheel(true);
        minutePicker.setOnLongPressUpdateInterval(100);
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
        minutePicker.setFocusable(false);

        TextView colon = new TextView(getContext());
        colon.setText(" : ");

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        linearLayout.addView(hourPicker, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(colon, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.addView(minutePicker, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return linearLayout;
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
//            if (inputMethodManager.isActive(hourPicker)) {
//                Log.d(TAG,"hourPicker.clearFocus");
            hourPicker.clearFocus();
//                inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
//            } else if (inputMethodManager.isActive(minutePicker)) {
//                Log.d(TAG,"minutePicker.clearFocus");
            minutePicker.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
//            }
        }
    }

    private RadioGroup initRadioGroup() {
        RadioButton overTimeButton = new RadioButton(getContext());
        overTimeButton.setText("over time");
        overTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overTime = true;
            }
        });
        overTimeButton.setId(OVER_TIME_BUTTON_ID);

        RadioButton underTimeButton = new RadioButton(getContext());
        underTimeButton.setText("under time");
        underTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overTime = false;
            }
        });
        underTimeButton.setId(UNDER_TIME_BUTTON_ID);

        radioGroup = new RadioGroup(getContext());
        radioGroup.addView(overTimeButton);
        radioGroup.addView(underTimeButton);
        radioGroup.clearCheck();
        radioGroup.check(OVER_TIME_BUTTON_ID);
        return radioGroup;
    }

    @Override
    protected void onBindDialogView(View v) {
        try {
            super.onBindDialogView(v);
            hourPicker.setValue(lastHour);
            minutePicker.setValue(lastMinute);
            if (overTime) {
                radioGroup.clearCheck();
                radioGroup.check(OVER_TIME_BUTTON_ID);
            } else {
                radioGroup.clearCheck();
                radioGroup.check(UNDER_TIME_BUTTON_ID);
            }
        } catch (Exception e) {
            Log.e(TAG, "onBindDialogView", e);
        }
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
