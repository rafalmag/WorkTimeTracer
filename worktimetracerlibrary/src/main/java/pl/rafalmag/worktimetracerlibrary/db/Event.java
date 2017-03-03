package pl.rafalmag.worktimetracerlibrary.db;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import pl.rafalmag.worktimetracerlibrary.EventSourcingPersistenceManager;

/**
 * All events should be stored in the same table.
 * To distinguish event class use typeClass.
 */
@DatabaseTable(tableName = "events")
public class Event {

    private static final String TAG = Event.class.getCanonicalName();

    // id is generated by the database and set on the object automagically
    @DatabaseField(id = true)
    protected Integer id;
    @DatabaseField
    protected Date date = new Date();

    @DatabaseField
    protected int version = 1;

    @DatabaseField
    protected String data = "";

    @DatabaseField
    protected String typeClass = getClass().getCanonicalName();

    /**
     * Needed by ormlite
     */
    @Deprecated
    public Event() {
        this("");
    }

    public Event(String data) {
        this.data = data;
    }

    public Event(Event event) {
        this.data = event.data;
        this.date = event.date;
        this.id = event.id;
        this.version = event.version;
    }

    public Integer getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public int getVersion() {
        return version;
    }

    public String getData() {
        return data;
    }

    public String getTypeClass() {
        return typeClass;
    }

    @Override
    public String toString() {
        return "Event{" +
                "typeClass='" + typeClass + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public void applyTo(EventSourcingPersistenceManager.ValueHolder valueHolder) {
        Log.e(TAG, "operation unsupported in raw event");
    }
}
