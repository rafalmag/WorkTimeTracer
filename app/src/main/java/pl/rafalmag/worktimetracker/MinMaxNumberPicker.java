package pl.rafalmag.worktimetracker;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.NumberPicker;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)//For backward-compability
public class MinMaxNumberPicker extends NumberPicker {

    public MinMaxNumberPicker(Context context) {
        super(context);
    }

    public MinMaxNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttributeSet(attrs);
    }

    public MinMaxNumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        processAttributeSet(attrs);
    }

    private void processAttributeSet(AttributeSet attrs) {
        //This method reads the parameters given in the xml file and sets the properties according to it
        setMinValue(attrs.getAttributeIntValue(null, "minValue", 0));
        setMaxValue(attrs.getAttributeIntValue(null, "maxValue", 0));
        setWrapSelectorWheel(attrs.getAttributeBooleanValue(null, "wrapSelectorWheel", true));
    }
}