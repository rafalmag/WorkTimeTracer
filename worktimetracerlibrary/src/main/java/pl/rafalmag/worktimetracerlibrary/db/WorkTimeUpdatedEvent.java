package pl.rafalmag.worktimetracerlibrary.db;

import org.joda.time.Minutes;
import org.json.JSONException;
import org.json.JSONObject;

public class WorkTimeUpdatedEvent extends Event {

    private static final String WORK_TIME = "work_time";

    // called by reflection
    public WorkTimeUpdatedEvent(Event event) {
        super(event);
    }

    public WorkTimeUpdatedEvent(Minutes workTime) {
        super(createData(workTime).toString());
    }

    private static JSONObject createData(Minutes workTime) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(WORK_TIME, workTime.getMinutes());
        } catch (JSONException e) {
            throw new IllegalStateException("Could not write json, because of " + e.getMessage(), e);
        }
        return jsonObject;
    }

    public Minutes getWorkTime() {
        try {
            JSONObject jsonObject = new JSONObject(data);
            return Minutes.minutes(jsonObject.getInt(WORK_TIME));
        } catch (JSONException e) {
            throw new IllegalStateException("Could not get work time from data " + data
                    + ", because of " + e.getMessage(), e);
        }
    }
}
