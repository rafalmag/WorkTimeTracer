package pl.rafalmag.worktimetracerlibrary.db;

import android.util.Log;

import org.joda.time.Minutes;

import pl.rafalmag.worktimetracerlibrary.DateUtils;
import pl.rafalmag.worktimetracerlibrary.EventSourcingPersistenceManager;

public class LogTimeEvent extends Event {

    private static final String TAG = LogTimeEvent.class.getCanonicalName();

    // called by reflection
    public LogTimeEvent(Event event) {
        super(event);
    }
    public LogTimeEvent() {
        super("");
    }

    @Override
    public String toString() {
        return "Log time event";
    }

    public void apply(EventSourcingPersistenceManager.ValueHolder valueHolder) {
        Minutes diff = DateUtils.diff(valueHolder.getStartTime(), valueHolder.getStopTime());
        Minutes todayOvertime = diff.minus(valueHolder.getWorkTime());
        Minutes totalOvertime = valueHolder.getOvertime();
        Minutes overtime =  totalOvertime.plus(todayOvertime);
        valueHolder.setOvertime(overtime);
        Log.i(TAG, "Logging work - new overtime " + DateUtils.minutesToText(overtime)
                + ", diff " + DateUtils.minutesToText(diff));
    }
}
