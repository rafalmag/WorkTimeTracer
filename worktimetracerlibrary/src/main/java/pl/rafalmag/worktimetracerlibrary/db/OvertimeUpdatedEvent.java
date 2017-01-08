package pl.rafalmag.worktimetracerlibrary.db;

import org.joda.time.Minutes;
import org.json.JSONException;
import org.json.JSONObject;

public class OvertimeUpdatedEvent extends Event {

    private static final String TOTAL_OVER_HOURS_AS_MINUTES = "total_over_hours_as_mins";

    // called by reflection
    public OvertimeUpdatedEvent(Event event) {
        super(event);
    }

    public OvertimeUpdatedEvent(Minutes overtime) {
        super(createData(overtime).toString());
    }

    private static JSONObject createData(Minutes overtime) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TOTAL_OVER_HOURS_AS_MINUTES, overtime.getMinutes());
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
}
