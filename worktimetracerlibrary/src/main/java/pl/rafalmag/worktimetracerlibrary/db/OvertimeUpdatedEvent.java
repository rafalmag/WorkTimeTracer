package pl.rafalmag.worktimetracerlibrary.db;

import org.joda.time.Minutes;
import org.json.JSONException;
import org.json.JSONObject;

import pl.rafalmag.worktimetracerlibrary.DateUtils;

public class OvertimeUpdatedEvent extends Event {

    private static final String TOTAL_OVER_HOURS_AS_MINUTES = "total_over_hours_as_mins";
    private static final String OLD_TOTAL_OVER_HOURS_AS_MINUTES = "old_total_over_hours_as_mins";

    // called by reflection
    public OvertimeUpdatedEvent(Event event) {
        super(event);
    }

    public OvertimeUpdatedEvent(Minutes oldOvertime, Minutes newOvertime) {
        super(createData(oldOvertime, newOvertime).toString());
    }

    private static JSONObject createData(Minutes oldOvertime, Minutes newOvertime) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TOTAL_OVER_HOURS_AS_MINUTES, newOvertime.getMinutes());
            jsonObject.put(OLD_TOTAL_OVER_HOURS_AS_MINUTES, oldOvertime.getMinutes());
        } catch (JSONException e) {
            throw new IllegalStateException("Could not write json, because of " + e.getMessage(), e);
        }
        return jsonObject;
    }

    public Minutes getOvertime() {
        try {
            JSONObject jsonObject = new JSONObject(data);
            return Minutes.minutes(jsonObject.getInt(TOTAL_OVER_HOURS_AS_MINUTES));
        } catch (JSONException e) {
            throw new IllegalStateException("Could not get overtime from data " + data
                    + ", because of " + e.getMessage(), e);
        }
    }

    public Minutes getOldOvertime() {
        try {
            JSONObject jsonObject = new JSONObject(data);
            return Minutes.minutes(jsonObject.getInt(OLD_TOTAL_OVER_HOURS_AS_MINUTES));
        } catch (JSONException e) {
            throw new IllegalStateException("Could not get old overtime from data " + data
                    + ", because of " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        Minutes diff = getOvertime().minus(getOldOvertime());
        String suffix;
        if (diff.equals(Minutes.ZERO)) {
            suffix = "";
        } else {
            suffix = " (" + DateUtils.minutesToText(diff) + ")";
        }
        return DateUtils.minutesToText(getOvertime()) + suffix;
    }
}
