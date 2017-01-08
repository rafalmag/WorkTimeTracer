package pl.rafalmag.worktimetracerlibrary.db;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;

public class EventParser {

    public Event parseEvent(Event event) {
        try {
            JSONObject jsonObject = new JSONObject(event.getData());
            String type = jsonObject.getString(Event.TYPE);
            try {
                Constructor<?> constructor = Class.forName(type)
                        .getConstructor(Event.class);
                return (Event) constructor.newInstance(event);
            } catch (Exception e) {
                throw new IllegalStateException("Could not create an event for class "
                        + type + ", because of " + e.getMessage(), e);
            }
        } catch (JSONException e) {
            throw new IllegalStateException("Could not parse event data " +
                    event.getData() + ", because of " + e.getMessage(), e);
        }
    }
}
