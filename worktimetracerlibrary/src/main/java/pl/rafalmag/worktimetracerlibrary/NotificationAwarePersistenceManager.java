package pl.rafalmag.worktimetracerlibrary;

import android.content.Context;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.MutableDateTime;

import pl.rafalmag.worktimetracerlibrary.notification.WorkEndNotificationScheduler;

public class NotificationAwarePersistenceManager implements PersistenceManager {

    private final Context context;
    private final PersistenceManager persistenceManager;
    private final WorkEndNotificationScheduler workEndNotificationScheduler = new WorkEndNotificationScheduler();

    public NotificationAwarePersistenceManager(PersistenceManager persistenceManager, Context context) {
        this.persistenceManager = persistenceManager;
        this.context = context;
    }

    @Override
    public Time loadStartTime() {
        return persistenceManager.loadStartTime();
    }

    @Override
    public Time loadStopTime() {
        return persistenceManager.loadStopTime();
    }

    @Override
    public void saveStartStopTime(Time startTime, Time stopTime) {
        persistenceManager.saveStartStopTime(startTime, stopTime);
        DateTime expectedEndTime = getExpectedEndTime(startTime, getWorkTime());
        workEndNotificationScheduler.scheduleWorkEndNotification(context, expectedEndTime);
    }

    @NonNull
    private DateTime getExpectedEndTime(Time startTime, Minutes workTime) {
        MutableDateTime expectedEndTime = new MutableDateTime();
        expectedEndTime.setHourOfDay(startTime.getHours());
        expectedEndTime.setMinuteOfHour(startTime.getMinutes());
        expectedEndTime.addMinutes(workTime.getMinutes());
        return expectedEndTime.toDateTime();
    }

    @Override
    public void saveOvertime(Minutes newOverHours) {
        persistenceManager.saveOvertime(newOverHours);
    }

    @Override
    public void logWork() {
        persistenceManager.logWork();
        workEndNotificationScheduler.cancelWorkEndNotification(context);
    }

    @Override
    public Minutes getOvertime() {
        return persistenceManager.getOvertime();
    }

    @Override
    public Minutes getWorkTime() {
        return persistenceManager.getWorkTime();
    }

    @Override
    public void saveWorkTime(Minutes workTime) {
        persistenceManager.saveWorkTime(workTime);
        DateTime expectedEndTime = getExpectedEndTime(loadStartTime(),workTime);
        workEndNotificationScheduler.scheduleWorkEndNotification(context, expectedEndTime);
    }
}
