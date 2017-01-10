package pl.rafalmag.worktimetracerlibrary.db;

import android.util.Log;

import org.joda.time.Minutes;
import org.json.JSONException;
import org.json.JSONObject;

import pl.rafalmag.worktimetracerlibrary.DateUtils;
import pl.rafalmag.worktimetracerlibrary.EventSourcingPersistenceManager;
import pl.rafalmag.worktimetracerlibrary.Time;

public class LogTimeEvent extends Event {

    private static final String TAG = LogTimeEvent.class.getCanonicalName();
    private static final String MESSAGE = "MESSAGE";

    // called by reflection
    @SuppressWarnings("unused")
    public LogTimeEvent(Event event) {
        super(event);
    }

    public LogTimeEvent(Time startTime, Time stopTime, Minutes workTime, Minutes overtime) {
        super(createData(startTime, stopTime, workTime, overtime).toString());
    }

    private static JSONObject createData(Time startTime, Time stopTime, Minutes workTime, Minutes overtime) {
        try {
            JSONObject jsonObject = new JSONObject();
            Minutes diff = calculateDiff(startTime, stopTime);
            Minutes todayOvertime = calculateTodayOvertime(diff, workTime);
            Minutes newOvertime = calculateNewOvertime(overtime, todayOvertime);
            String message = "Logging work - new overtime " + DateUtils.minutesToText(newOvertime)
                    + " (" + DateUtils.minutesToText(todayOvertime) + ")";
            jsonObject.put(MESSAGE, message);
            return jsonObject;
        } catch (JSONException e) {
            throw new IllegalStateException("Could not write json, because of " + e.getMessage(), e);
        }
    }

    public String getMessage() {
        try {
            JSONObject jsonObject = new JSONObject(data);
            return jsonObject.getString(MESSAGE);
        } catch (JSONException e) {
            throw new IllegalStateException("Could not get message from data " + data
                    + ", because of " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return getMessage();
    }

    public void apply(EventSourcingPersistenceManager.ValueHolder valueHolder) {
        Minutes diff = calculateDiff(valueHolder.getStartTime(), valueHolder.getStopTime());
        Minutes todayOvertime = calculateTodayOvertime(diff, valueHolder.getWorkTime());
        Minutes overtime = calculateNewOvertime(valueHolder.getOvertime(), todayOvertime);
        valueHolder.setOvertime(overtime);
        Log.i(TAG, "Logging work - new overtime " + DateUtils.minutesToText(overtime)
                + " (" + DateUtils.minutesToText(todayOvertime) + ")");
    }

    private static Minutes calculateNewOvertime(Minutes overtime, Minutes todayOvertime) {
        return overtime.plus(todayOvertime);
    }

    private static Minutes calculateTodayOvertime(Minutes diff, Minutes workTime) {
        return diff.minus(workTime);
    }

    private static Minutes calculateDiff(Time startTime, Time stopTime) {
        return DateUtils.diff(startTime, stopTime);
    }
}
