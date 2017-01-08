package pl.rafalmag.worktimetracerlibrary.db;

import java.lang.reflect.Constructor;

public class EventParser {

    public <T extends Event> T parseEvent(Event event) {
        String type = event.getTypeClass();
        try {
            Constructor<?> constructor = Class.forName(type).getConstructor(Event.class);
            return (T) constructor.newInstance(event);
        } catch (Exception e) {
            throw new IllegalStateException("Could not create an event for class "
                    + type + ", because of " + e.getMessage(), e);
        }
    }
}
