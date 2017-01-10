package pl.rafalmag.worktimetracerlibrary.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.joda.time.Hours;
import org.joda.time.Minutes;

import java.sql.SQLException;

import pl.rafalmag.worktimetracerlibrary.R;
import pl.rafalmag.worktimetracerlibrary.Time;

public class WorkTimeTracerOpenHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "WorkTimeTracer.db";
    private static final int DATABASE_VERSION = 6;

    public WorkTimeTracerOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, Event.class);
            // init values
            getEventDao().create(new WorkTimeUpdatedEvent(Hours.EIGHT.toStandardMinutes()));
            getEventDao().create(new OvertimeUpdatedEvent(Minutes.ZERO, Minutes.ZERO));
            getEventDao().create(new StartStopUpdatedEvent(new Time(8, 0), new Time(16, 0)));
        } catch (SQLException e) {
            Log.e(WorkTimeTracerOpenHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            Log.i(WorkTimeTracerOpenHelper.class.getName(),
                    "onUpgrade from version " + oldVersion + " to version " + newVersion);
            TableUtils.dropTable(connectionSource, Event.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new IllegalStateException("Can't drop database, because of " + e.getMessage(), e);
        }
    }

    public Dao<Event, Integer> getEventDao() {
        try {
            return getDao(Event.class);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not get dao for Event, because of "
                    + e.getMessage(), e);
        }
    }

}
