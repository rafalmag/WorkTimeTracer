package pl.rafalmag.worktimetracerlibrary.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.sql.SQLException;

import pl.rafalmag.worktimetracerlibrary.db.EventsService;
import pl.rafalmag.worktimetracerlibrary.db.LogTimeEvent;
import pl.rafalmag.worktimetracerlibrary.db.StartStopUpdatedEvent;

// to start an Alarm When the Device Boots
public class WorkEndNotificationBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            EventsService eventsService = new EventsService(context);
            try {
                LogTimeEvent logTimeEvent = eventsService.getLastLogEvent();
                StartStopUpdatedEvent startStopUpdatedEvent = eventsService.getLastStartStopEvent();
//                startStopUpdatedEvent.getStopTime()
                if (logTimeEvent.getDate().before(startStopUpdatedEvent.getDate())) {
                    // TODO Set the alarm here.
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}
