package pl.rafalmag.worktimetracerlibrary.db;

import android.content.Context;
import android.support.annotation.Nullable;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class EventsService {

    private final Dao<Event, Integer> eventDao;

    public EventsService(Context context) {
        WorkTimeTracerOpenHelper workTimeTracerOpenHelper = OpenHelperManager.getHelper(context, WorkTimeTracerOpenHelper.class);
        eventDao = workTimeTracerOpenHelper.getEventDao();
    }

    public List<Event> getEvents(Iterable<String> typesToSelect) throws SQLException {
        return eventDao
                .queryBuilder()
                .orderBy("date", true)
                .where().in("typeClass", typesToSelect)
                .query();
    }

    public List<String[]> getTypes() throws SQLException {
        return eventDao.queryRaw("select distinct typeClass from events").getResults();
    }

    @Nullable
    public LogTimeEvent getLastLogEvent() throws SQLException {
        return getLastEventOfType(LogTimeEvent.class);
    }

    @Nullable
    public StartStopUpdatedEvent getLastStartStopEvent() throws SQLException {
        return getLastEventOfType(StartStopUpdatedEvent.class);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private <T extends Event> T getLastEventOfType(Class<T> clazz) throws SQLException {
        Event event = eventDao
                .queryBuilder()
                .orderBy("date", false)
                .where().eq("typeClass", clazz.getCanonicalName())
                .queryForFirst();
        if (event == null) {
            return null;
        } else {
            return (T) event;
        }
    }
}
