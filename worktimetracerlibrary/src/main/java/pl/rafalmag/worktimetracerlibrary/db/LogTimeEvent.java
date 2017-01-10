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

    public void apply(EventSourcingPersistenceManager.ValueAccessor valueSetter) {
        Minutes diff = DateUtils.diff(valueSetter.getStartTime(), valueSetter.getStopTime());
        Minutes todayOvertime = diff.minus(valueSetter.getWorkTime());
        Minutes totalOvertime = valueSetter.getOvertime();
        Minutes overtime =  totalOvertime.plus(todayOvertime);
        valueSetter.setOvertime(overtime);
        Log.i(TAG, "Logging work - new overtime " + DateUtils.minutesToText(overtime)
                + ", diff " + DateUtils.minutesToText(diff));
    }
}
