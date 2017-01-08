package pl.rafalmag.worktimetracerlibrary.db;


import org.json.JSONException;
import org.json.JSONObject;

import pl.rafalmag.worktimetracerlibrary.Time;

public class StartStopUpdatedEvent extends Event {
    private static final String START_HOUR = "START_HOUR";
    private static final String START_MINS = "START_MINS";
    private static final String STOP_HOUR = "STOP_HOUR";
    private static final String STOP_MINS = "STOP_MINS";

    // called by reflection
    public StartStopUpdatedEvent(Event event) {
        super(event);
    }

    public StartStopUpdatedEvent(Time startTime, Time stopTime) {
        super(createData(startTime, stopTime).toString());
    }

    private static JSONObject createData(Time startTime, Time stopTime) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(START_HOUR, startTime.getHours());
            jsonObject.put(START_MINS, startTime.getMinutes());
            jsonObject.put(STOP_HOUR, stopTime.getHours());
            jsonObject.put(STOP_MINS, stopTime.getMinutes());
        } catch (JSONException e) {
            throw new IllegalStateException("Could not write json, because of " + e.getMessage(), e);
        }
        return jsonObject;
    }

    public Time getStartTime() {
        try {
            JSONObject jsonObject = new JSONObject(data);
            return new Time(jsonObject.getInt(START_HOUR), jsonObject.getInt(START_MINS));
        } catch (JSONException e) {
            throw new IllegalStateException("Could not get start time from data " + data
                    + ", because of " + e.getMessage(), e);
        }
    }

    public Time getStopTime() {
        try {
            JSONObject jsonObject = new JSONObject(data);
            return new Time(jsonObject.getInt(STOP_HOUR), jsonObject.getInt(STOP_MINS));
        } catch (JSONException e) {
            throw new IllegalStateException("Could not get stop time from data " + data
                    + ", because of " + e.getMessage(), e);
        }
    }
}
